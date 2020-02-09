package log4j2;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author: xiang.dai
 * @date: 2020/2/8
 * @description:
 *
 * JMH: http://openjdk.java.net/projects/code-tools/jmh/
 * JMH-samples: http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 *
 * 结论： 文本长短 很影响日志输出性能
 * log4j的配置 RandomAccessFile 比 file高很多
 *  <Pattern>%d %p %c{1.} [%t] %m%n</Pattern> 带有类信息很影响配置
 *
 *  注：如果使用了sl4j-api 则输出行号会无效（配置也不生效）
 *  如果需要开启disrupotr需要引入maven包
 *  在jvm启动参数中加入 -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
 *
 *  启动garbage-free
 *  -Dlog4j2.enable.threadlocals=true -Dlog4j2.enable.direct.encoders=true
 */
@State(Scope.Benchmark)
public class Log4j2PerformanceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(Log4j2PerformanceTest.class);

    /**
     * 让每次打印的日志信息都不同，避免被缓存等操作
     */
    private static int i;

    @Param({"shortLogInfo", "LongLogInfoLongLogInfoLongLogInfoLongLogInfoLongLogInfoLongLogInfoLongLogInfo"})
    private String logOutputStr;

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void testThroughput() {

        LOGGER.info(logOutputStr + i++);
    }

    /**
     * 默认warmup 每次10秒 5次
     * @param args
     * @throws RunnerException
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Log4j2PerformanceTest.class.getSimpleName())
                .forks(1).threads(1).param("logOutputStr", "shortLogInfo")
                .build();

        new Runner(opt).run();

//        Benchmark                                                                                            (logOutputStr)   Mode  Cnt        Score       Error  Units
//        Log4j2PerformanceTest.testThroughput                                                                   shortLogInfo  thrpt    5  1187822.282 ± 70363.082  ops/s
//        Log4j2PerformanceTest.testThroughput  LongLogInfoLongLogInfoLongLogInfoLongLogInfoLongLogInfoLongLogInfoLongLogInfo  thrpt    5   934862.883 ± 11426.688  ops/s
    }
}
