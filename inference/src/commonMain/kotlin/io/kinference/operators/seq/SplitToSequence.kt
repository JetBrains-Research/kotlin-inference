package io.kinference.operators.seq

import io.kinference.attributes.Attribute
import io.kinference.data.ONNXDataType
import io.kinference.data.seq.ONNXSequence
import io.kinference.data.tensors.Tensor
import io.kinference.data.tensors.splitWithAxis
import io.kinference.graph.Context
import io.kinference.graph.ProfilingContext
import io.kinference.operators.*
import io.kinference.protobuf.message.AttributeProto
import io.kinference.protobuf.message.TensorProto
import io.kinference.types.ValueInfo
import io.kinference.types.ValueTypeInfo.SequenceTypeInfo
import kotlin.time.ExperimentalTime

@ExperimentalTime
class SplitToSequence(attributes: Map<String, Attribute<Any>>, inputs: List<String>, outputs: List<String>)
    : Operator<Tensor, ONNXSequence>(INFO, attributes, inputs, outputs) {
    companion object {
        private const val DEFAULT_SPLIT_LENGTH = 1
        private val TYPE_CONSTRAINTS = ALL_DATA_TYPES

        private val ATTRIBUTES_INFO = listOf(
            AttributeInfo("axis", setOf(AttributeProto.AttributeType.INT), false, default = 0L),
            AttributeInfo("keepdims", setOf(AttributeProto.AttributeType.INT), false, default = 1L)
        )

        private val INPUTS_INFO = listOf(
            IOInfo(0, TYPE_CONSTRAINTS, "input", optional = false),
            IOInfo(1, setOf(TensorProto.DataType.INT64, TensorProto.DataType.INT32), "split", optional = true)
        )

        private val OUTPUTS_INFO = listOf(IOInfo(0, TYPE_CONSTRAINTS, "output_sequence", optional = false, onnxDataType = ONNXDataType.ONNX_SEQUENCE))

        private val INFO = OperatorInfo("SplitToSequence", ATTRIBUTES_INFO, INPUTS_INFO, OUTPUTS_INFO)
    }

    private val axis: Int by attribute { it: Number -> it.toInt() }
    private val keepDims: Boolean by attribute("keepdims") { it: Number -> it.toInt() == 1 }


    override fun apply(context: Context, inputs: List<Tensor?>, profilingContext: ProfilingContext?): List<ONNXSequence?> {
        val parts = inputs.elementAtOrNull(1)

        val input = inputs[0]!!
        val tensors = if (parts == null) {
            input.splitWithAxis(input.data.shape[axis], axis, keepDims)
        } else {
            input.splitWithAxis(parts, axis)
        }

        return listOf(ONNXSequence(tensors, ValueInfo(SequenceTypeInfo(tensors[0].info.typeInfo), "output_sequence")))
    }
}
