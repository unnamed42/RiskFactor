package com.tjh.riskfactor.entity.cvt

import javax.persistence.Converter
import javax.persistence.AttributeConverter

import com.tjh.riskfactor.entity.form.QuestionOptions
import com.tjh.riskfactor.util.mapper

@Converter
class QuestionOptionsConverter: AttributeConverter<QuestionOptions, String> {

    override fun convertToDatabaseColumn(attribute: QuestionOptions?): String? =
        attribute?.let { mapper.writeValueAsString(attribute) }?.takeIf { it.length > 2 }

    override fun convertToEntityAttribute(dbData: String?): QuestionOptions? =
        dbData?.takeIf { it.length > 2 }?.let { mapper.readValue(it, QuestionOptions::class.java) }
}
