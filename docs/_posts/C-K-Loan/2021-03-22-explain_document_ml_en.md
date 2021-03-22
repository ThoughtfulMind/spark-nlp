---
layout: model
title: Explain Document ML Pipeline for English
author: John Snow Labs
name: explain_document_ml
date: 2021-03-22
tags: [open_source, english, explain_document_ml, pipeline, en]
task: [Named Entity Recognition, Lemmatization, Part of Speech Tagging]
language: en
edition: Spark NLP 3.0.0
spark_version: 3.0
article_header:
  type: cover
use_language_switcher: "Python-Scala-Java"
---

## Description

The explain_document_ml is a pretrained pipeline that we can use to process text with a simple pipeline that performs basic processing steps.
         It performs most of the common text processing tasks on your dataframe

{:.btn-box}
<button class="button button-orange" disabled>Live Demo</button>
[Open in Colab](https://colab.research.google.com/github/JohnSnowLabs/spark-nlp-workshop/blob/2da56c087da53a2fac1d51774d49939e05418e57/jupyter/annotation/english/explain-document-dl/Explain%20Document%20DL.ipynb){:.button.button-orange.button-orange-trans.co.button-icon}
[Download](https://s3.amazonaws.com/auxdata.johnsnowlabs.com/public/models/explain_document_ml_en_3.0.0_3.0_1616415468933.zip){:.button.button-orange.button-orange-trans.arr.button-icon}

## How to use



<div class="tabs-box" markdown="1">
{% include programmingLanguageSelectScalaPythonNLU.html %}
```python

from sparknlp.pretrained import PretrainedPipelinein
pipeline = PretrainedPipeline('explain_document_ml', lang = 'en')
annotations =  pipeline.fullAnnotate(""Hello from John Snow Labs ! "")[0]
annotations.keys()

```
```scala

val pipeline = new PretrainedPipeline("explain_document_ml", lang = "en")
val result = pipeline.fullAnnotate("Hello from John Snow Labs ! ")(0)


```

{:.nlu-block}
```python

import nlu
text = [""Hello from John Snow Labs ! ""]
result_df = nlu.load('en.explain').predict(text)
result_df
    
```
</div>

## Results

```bash
|    | document                         | sentence                         | token                                            | spell                                           | lemmas                                          | stems                                          | pos                                    |
|---:|:---------------------------------|:---------------------------------|:-------------------------------------------------|:------------------------------------------------|:------------------------------------------------|:-----------------------------------------------|:---------------------------------------|
|  0 | ['Hello fronm John Snwow Labs!'] | ['Hello fronm John Snwow Labs!'] | ['Hello', 'fronm', 'John', 'Snwow', 'Labs', '!'] | ['Hello', 'front', 'John', 'Snow', 'Labs', '!'] | ['Hello', 'front', 'John', 'Snow', 'Labs', '!'] | ['hello', 'front', 'john', 'snow', 'lab', '!'] | ['UH', 'NN', 'NNP', 'NNP', 'NNP', '.'] ||    | document   | sentence   | token     | spell     | lemmas    | stems     | pos    |

```

{:.model-param}
## Model Information

{:.table-model}
|---|---|
|Model Name:|explain_document_ml|
|Type:|pipeline|
|Compatibility:|Spark NLP 3.0.0+|
|License:|Open Source|
|Edition:|Official|
|Language:|en|

## Included Models

This pipeline consists of : 
, 
,  - document 
,- SENTENCE 
,- REGEX_TOKENIZER 
,- SPELL 
,- LEMMATIZER 
,- STEMMER 
,- POS 
,This pipeline consists of : 
, 
,- document 
,- SENTENCE 
,- REGEX_TOKENIZER 
,- SPELL 
,- LEMMATIZER 
,- STEMMER 
,- POS 
,This pipeline consists of : 
, 
,- document 
,- SENTENCE 
,- REGEX_TOKENIZER 
,- SPELL 
,- LEMMATIZER 
,- STEMMER 
,- POS 
,This pipeline consists of : 
, 
,- document 
,- SENTENCE 
,- REGEX_TOKENIZER 
,- SPELL 
,- LEMMATIZER 
,- STEMMER 
,- POS 
,This pipeline consists of : 
, 
,- document 
,- SENTENCE 
,- REGEX_TOKENIZER 
,- SPELL 
,- LEMMATIZER 
,- STEMMER 
,- POS 
,This pipeline consists of : 
, 
,- document 
,- SENTENCE 
,- REGEX_TOKENIZER 
,- SPELL 
,- LEMMATIZER 
,- STEMMER 
,- POS 
,This pipeline consists of : 
, 
,- document 
,- SENTENCE 
,- REGEX_TOKENIZER 
,- SPELL 
,- LEMMATIZER 
,- STEMMER 
,- POS 
,This pipeline consists of : 
, 
,- document 
,- SENTENCE 
,- REGEX_TOKENIZER 
,- SPELL 
,- LEMMATIZER 
,- STEMMER 
,- POS