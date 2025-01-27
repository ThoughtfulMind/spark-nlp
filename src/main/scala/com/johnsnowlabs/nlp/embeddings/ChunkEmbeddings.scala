package com.johnsnowlabs.nlp.embeddings

import com.johnsnowlabs.nlp.annotators.common.WordpieceEmbeddingsSentence
import com.johnsnowlabs.nlp.{Annotation, AnnotatorModel, HasSimpleAnnotate}
import org.apache.spark.ml.param.{BooleanParam, Param}
import org.apache.spark.ml.util.{DefaultParamsReadable, Identifiable}
import org.apache.spark.sql.DataFrame

import scala.collection.Map

object PoolingStrategy {

  object AnnotatorType {
    val AVERAGE = "AVERAGE"
    val SUM = "SUM"
  }

}

/** This annotator utilizes WordEmbeddings or BertEmbeddings to generate chunk embeddings from either Chunker, NGramGenerator, or NerConverter outputs.
  *
  * TIP:
  *
  * How to explode and convert these embeddings into Vectors or what’s known as Feature column so it can be used in Spark ML regression or clustering functions:
  *
  * {{{
  * import org.apache.spark.ml.linalg.{Vector, Vectors}
  *
  * // Let's create a UDF to take array of embeddings and output Vectors
  * val convertToVectorUDF = udf((matrix : Seq[Float]) => {
  *     Vectors.dense(matrix.toArray.map(_.toDouble))
  * })
  *
  * // Now let's explode the sentence_embeddings column and have a new feature column for Spark ML
  * pipelineDF.select(explode($"chunk_embeddings.embeddings").as("chunk_embeddings_exploded"))
  * .withColumn("features", convertToVectorUDF($"chunk_embeddings_exploded"))
  * }}}
  *
  * See [[https://github.com/JohnSnowLabs/spark-nlp/blob/master/src/test/scala/com/johnsnowlabs/nlp/embeddings/ChunkEmbeddingsTestSpec.scala]] for further reference on how to use this API.
  *
  * @groupname anno Annotator types
  * @groupdesc anno Required input and expected output annotator types
  * @groupname Ungrouped Members
  * @groupname param Parameters
  * @groupname setParam Parameter setters
  * @groupname getParam Parameter getters
  * @groupname Ungrouped Members
  * @groupprio param  1
  * @groupprio anno  2
  * @groupprio Ungrouped 3
  * @groupprio setParam  4
  * @groupprio getParam  5
  * @groupdesc Parameters A list of (hyper-)parameter keys this annotator can take. Users can set and get the parameter values through setters and getters, respectively.
  **/
class ChunkEmbeddings (override val uid: String) extends AnnotatorModel[ChunkEmbeddings] with HasSimpleAnnotate[ChunkEmbeddings] {

  import com.johnsnowlabs.nlp.AnnotatorType._

  /** Output annotator type : WORD_EMBEDDINGS
    *
    * @group anno
    **/
  override val outputAnnotatorType: AnnotatorType = WORD_EMBEDDINGS
  /** Input annotator type : CHUNK, WORD_EMBEDDINGS
    *
    * @group anno
    **/
  override val inputAnnotatorTypes: Array[AnnotatorType] = Array(CHUNK, WORD_EMBEDDINGS)
  /** Choose how you would like to aggregate Word Embeddings to Chunk Embeddings: AVERAGE or SUM
    *
    * @group param
    **/
  val poolingStrategy = new Param[String](this, "poolingStrategy", "Choose how you would like to aggregate Word Embeddings to Chunk Embeddings: AVERAGE or SUM")
  /** Whether to discard default vectors for OOV words from the aggregation / pooling
    *
    * @group param
    **/
  val skipOOV = new BooleanParam(this, "skipOOV", "Whether to discard default vectors for OOV words from the aggregation / pooling")


  /** PoolingStrategy must be either AVERAGE or SUM
    *
    * @group setParam
    **/
  def setPoolingStrategy(strategy: String): this.type = {
    strategy.toLowerCase() match {
      case "average" => set(poolingStrategy, "AVERAGE")
      case "sum" => set(poolingStrategy, "SUM")
      case _ => throw new MatchError("poolingStrategy must be either AVERAGE or SUM")
    }
  }

  /** Whether to discard default vectors for OOV words from the aggregation / pooling
    *
    * @group setParam
    **/
  def setSkipOOV(value: Boolean): this.type = set(skipOOV, value)

  /** Choose how you would like to aggregate Word Embeddings to Chunk Embeddings: AVERAGE or SUM
    *
    * @group getParam
    **/
  def getPoolingStrategy = $(poolingStrategy)

  /** Whether to discard default vectors for OOV words from the aggregation / pooling
    *
    * @group getParam
    **/
  def getSkipOOV = $(skipOOV)

  setDefault(
    inputCols -> Array(CHUNK, WORD_EMBEDDINGS),
    outputCol -> "chunk_embeddings",
    poolingStrategy -> "AVERAGE",
    skipOOV -> true
  )

  /** Internal constructor to submit a random UID */
  def this() = this(Identifiable.randomUID("CHUNK_EMBEDDINGS"))

  private def calculateChunkEmbeddings(matrix : Array[Array[Float]]):Array[Float] = {
    val res = Array.ofDim[Float](matrix(0).length)
    matrix(0).indices.foreach {
      j =>
        matrix.indices.foreach {
          i =>
            res(j) += matrix(i)(j)
        }
        if($(poolingStrategy) == "AVERAGE")
          res(j) /= matrix.length
    }
    res
  }

  /**
    * takes a document and annotations and produces new annotations of this annotator's annotation type
    *
    * @param annotations Annotations that correspond to inputAnnotationCols generated by previous annotators if any
    * @return any number of annotations processed for every input annotation. Not necessary one to one relationship
    */
  override def annotate(annotations: Seq[Annotation]): Seq[Annotation] = {

    val documentsWithChunks = annotations
      .filter(token => token.annotatorType == CHUNK)

    val embeddingsSentences = WordpieceEmbeddingsSentence.unpack(annotations)

    documentsWithChunks.flatMap { chunk =>
      //TODO: Check why some chunks end up without WordEmbeddings
      val sentenceIdx = chunk.metadata.getOrElse("sentence", "0").toInt
      val chunkIdx = chunk.metadata.getOrElse("chunk", "0").toInt

      if (sentenceIdx < embeddingsSentences.length) {

        val tokensWithEmbeddings = embeddingsSentences(sentenceIdx).tokens.filter(
          token => token.begin >= chunk.begin && token.end <= chunk.end
        )

        val allEmbeddings = tokensWithEmbeddings.flatMap(tokenEmbedding =>
          if (!tokenEmbedding.isOOV || !$(skipOOV))
            Some(tokenEmbedding.embeddings)
          else
            None
        )

        val finalEmbeddings = if (allEmbeddings.length > 0) allEmbeddings else tokensWithEmbeddings.map(_.embeddings)

        Some(Annotation(
          annotatorType = outputAnnotatorType,
          begin = chunk.begin,
          end = chunk.end,
          result = chunk.result,
          metadata = Map(
            "sentence" -> sentenceIdx.toString,
            "chunk" -> chunkIdx.toString,
            "token" -> chunk.result.toString,
            "pieceId" -> "-1",
            "isWordStart" -> "true"
          ),
          embeddings = calculateChunkEmbeddings(finalEmbeddings)
        ))
      } else {
        None
      }
    }
  }

  override protected def afterAnnotate(dataset: DataFrame): DataFrame = {
    val embeddingsCol = Annotation.getColumnByType(dataset, $(inputCols), WORD_EMBEDDINGS)
    dataset.withColumn(getOutputCol, dataset.col(getOutputCol).as(getOutputCol, embeddingsCol.metadata))
  }

}

object ChunkEmbeddings extends DefaultParamsReadable[ChunkEmbeddings]