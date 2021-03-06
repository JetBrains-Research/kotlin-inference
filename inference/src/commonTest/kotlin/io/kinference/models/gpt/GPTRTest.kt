package io.kinference.models.gpt

import io.kinference.runners.AccuracyRunner
import io.kinference.runners.PerformanceRunner
import io.kinference.utils.TestRunner
import kotlin.test.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
class GPTRTest {
    @Test
    fun heavy_test_gpt_model() = TestRunner.runTest {
        AccuracyRunner.runFromS3("gpt2:r-completion:standard:v1")
    }

    @Test
    fun benchmark_test_gpt_performance() = TestRunner.runTest {
        PerformanceRunner.runFromS3("gpt2:r-completion:standard:v1")
    }


    @Test
    fun heavy_test_gpt_quantized_model() = TestRunner.runTest {
        AccuracyRunner.runFromS3("gpt2:r-completion:quantized:v1", delta = 2.4)
    }

    @Test
    fun benchmark_test_gpt_quantized_performance() = TestRunner.runTest {
        PerformanceRunner.runFromS3("gpt2:r-completion:quantized:v1")
    }
}
