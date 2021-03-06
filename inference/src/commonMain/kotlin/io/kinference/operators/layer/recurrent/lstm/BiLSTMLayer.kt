package io.kinference.operators.layer.recurrent.lstm

import io.kinference.ndarray.arrays.*
import io.kinference.ndarray.extensions.allocateNDArray
import io.kinference.operators.activations.Activation
import io.kinference.primitives.types.DataType
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class BiLSTMLayer(hiddenSize: Int, activations: List<String>): LSTMLayerBase(hiddenSize, activations, "bidirectional") {
    init {
        require(activations.size == 6)
    }

    private val forwardLayer = LSTMLayer(hiddenSize, activations.subList(0, 3), "forward")
    private val reverseLayer = LSTMLayer(hiddenSize, activations.subList(3, 6), "reverse")

    override fun apply(
        input: NumberNDArray,
        weights: NumberNDArray,
        recurrentWeights: NumberNDArray,
        bias: NumberNDArray?,
        sequenceLens: IntNDArray?,
        initialHiddenState: NumberNDArray?,
        initialCellState: NumberNDArray?,
        peepholes: NumberNDArray?,
        dataType: DataType
    ): Triple<NumberNDArray, NumberNDArray, NumberNDArray> {
        val seqLength = input.shape[0]
        val batchSize = input.shape[1]

        val forwardH = Activation.create(activations[2], dataType)
        val reverseH = Activation.create(activations[5], dataType)

        val forwardLSTMGates = LSTMGates.create(
            weights.view(0),
            recurrentWeights.view(0),
            bias?.view(0),
            peepholes?.view(0),
            batchSize, hiddenSize, dataType
        )

        val reverseLSTMGates = LSTMGates.create(
            weights.view(1),
            recurrentWeights.view(1),
            bias?.view(1),
            peepholes?.view(1),
            batchSize, hiddenSize, dataType
        )

        val lstmStates = LSTMStates(
            LSTMCellState(initialCellState, dataType, 2, batchSize, hiddenSize),
            LSTMHiddenState(initialHiddenState, dataType, 2, batchSize, hiddenSize, listOf(forwardH, reverseH))
        )

        val outputArray = allocateNDArray(dataType, intArrayOf(seqLength, 2, batchSize, hiddenSize)) as MutableNumberNDArray

        forwardLayer.apply(input, outputArray, lstmStates, forwardLSTMGates, sequenceLens, 0, seqLength, batchSize, dataType)
        reverseLayer.apply(input, outputArray, lstmStates, reverseLSTMGates, sequenceLens, 1, seqLength, batchSize, dataType)

        return Triple(outputArray, lstmStates.hiddenState.data, lstmStates.cellState.data)
    }
}
