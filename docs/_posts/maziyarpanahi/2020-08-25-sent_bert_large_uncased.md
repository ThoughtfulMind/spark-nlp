---
layout: model
title: BERT Sentence Embeddings (Large Uncased)
author: John Snow Labs
name: sent_bert_large_uncased
date: 2020-08-25
task: Embeddings
language: en
edition: Spark NLP 2.6.0
tags: [open_source, embeddings, en]
supported: false
article_header:
  type: cover
use_language_switcher: "Python-Scala-Java"
---

## Description
This model contains a deep bidirectional transformer trained on Wikipedia and the BookCorpus. The details are described in the paper "[BERT: Pre-training of Deep Bidirectional Transformers for Language Understanding](https://arxiv.org/abs/1810.04805)".

{:.btn-box}
<button class="button button-orange" disabled>Live Demo</button>
<button class="button button-orange" disabled>Open in Colab</button>
[Download](https://s3.amazonaws.com/auxdata.johnsnowlabs.com/public/models/sent_bert_large_uncased_en_2.6.0_2.4_1598347026632.zip){:.button.button-orange.button-orange-trans.arr.button-icon}

## How to use

<div class="tabs-box" markdown="1">

{% include programmingLanguageSelectScalaPythonNLU.html %}

```python
...
embeddings = BertEmbeddings.pretrained("sent_bert_large_uncased", "en") \
      .setInputCols("sentence") \
      .setOutputCol("sentence_embeddings")
nlp_pipeline = Pipeline(stages=[document_assembler, sentence_detector, embeddings])
pipeline_model = nlp_pipeline.fit(spark.createDataFrame([[""]]).toDF("text"))
result = pipeline_model.transform(spark.createDataFrame(pd.DataFrame({"text": ["I hate cancer, "Antibiotics aren't painkiller"]})))
```

```scala
...
val embeddings = BertEmbeddings.pretrained("sent_bert_large_uncased", "en")
      .setInputCols("sentence")
      .setOutputCol("sentence_embeddings")
val pipeline = new Pipeline().setStages(Array(document_assembler, sentence_detector, embeddings))
val result = pipeline.fit(Seq.empty["I hate cancer, "Antibiotics aren't painkiller"].toDS.toDF("text")).transform(data)
```

{:.nlu-block}
```python
import nlu

text = ["I hate cancer", "Antibiotics aren't painkiller"]
embeddings_df = nlu.load('en.embed_sentence.bert_large_uncased').predict(text, output_level='sentence')
embeddings_df
```

</div>

{:.h2_title}
## Results
```bash
	sentence                            en_embed_sentence_bert_large_uncased_embeddings
	
      I hate cancer 	                  [[-0.13290119171142578, -0.2996622622013092, -...
      Antibiotics aren't painkiller 	[[-0.13290119171142578, -0.2996622622013092, -...
```

{:.model-param}
## Model Information

{:.table-model}
|---|---|
|Model Name:|sent_bert_large_uncased|
|Type:|embeddings|
|Compatibility:|Spark NLP 2.6.0+|
|License:|Open Source|
|Edition:|Official|
|Input Labels:|[sentence]|
|Output Labels:|[sentence_embeddings]|
|Language:|[en]|
|Dimension:|1024|
|Case sensitive:|false|

{:.h2_title}
## Data Source
The model is imported from [https://tfhub.dev/google/bert_uncased_L-24_H-1024_A-16/1](https://tfhub.dev/google/bert_uncased_L-24_H-1024_A-16/1)
