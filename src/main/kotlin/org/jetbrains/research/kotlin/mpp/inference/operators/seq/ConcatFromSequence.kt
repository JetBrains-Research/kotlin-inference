package org.jetbrains.research.kotlin.mpp.inference.operators.seq

import org.jetbrains.research.kotlin.mpp.inference.attributes.Attribute
import org.jetbrains.research.kotlin.mpp.inference.data.seq.TensorSeq
import org.jetbrains.research.kotlin.mpp.inference.data.tensors.*
import org.jetbrains.research.kotlin.mpp.inference.operators.*

class ConcatFromSequence(attributes: Map<String, Attribute<Any>>)
    : Operator<TensorSeq, Tensor>("ConcatFromSequence", attributes, ATTRIBUTES_INFO, INPUTS_INFO, OUTPUTS_INFO) {
    companion object {
        private val TYPE_CONSTRAINTS = ALL_DATA_TYPES

        private val ATTRIBUTES_INFO = listOf(
            AttributeInfo("axis", setOf(AttributeProto.AttributeType.INT), true),
            AttributeInfo("new_axis", setOf(AttributeProto.AttributeType.INT), false, default = 0L)
        )

        private val INPUTS_INFO = listOf(InputInfo(0, TYPE_CONSTRAINTS, "input_sequence", true))

        private val OUTPUTS_INFO = listOf(OutputInfo(0, TYPE_CONSTRAINTS, "concat_result"))
    }

    override fun apply(inputs: Collection<TensorSeq>, numOutputs: Int): Collection<Tensor> {
        val axis = getAttributeValue("axis") as Long
        val newAxis = getAttributeValue("newAxis") as Long

        val srcTensors = inputs.first().data
        val tensor = if (newAxis == 1L) srcTensors.stack(axis.toInt()) else srcTensors.concatenate(axis.toInt())
        return listOf(tensor)
    }
}
