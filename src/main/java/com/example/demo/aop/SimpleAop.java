package com.example.demo.aop;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect     // 관점 : 흩어진 관심사를 하나로 묶은 것
@Component
@RequiredArgsConstructor
public class SimpleAop {
    private final Tracer tracer;

    // 실행될 위치나 시점을 지정
    //      * 리턴 타입
    //      com.example.demo.board.. 해당 패키지 및 하위 모든 패키지
    //      *.*(..) 모든 클래스의 모든 메소드 및 모든 매개변수
    @Pointcut("execution(* com.example.demo.board..*.*(..))")
    private void cut() {    // 포인트 컷을 적용할 이름을 설정
    }

    @Around("cut()")
    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();

        Span span = tracer.spanBuilder(className + "." + methodName).startSpan();
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("method.name", methodName);

            return joinPoint.proceed();
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    //    @Before("execution(* com.example.demo.board..*.*(..))") // PointCut을 안만들어두면 이렇게 설정해야 됨
    @Before("cut()") // 포인트 컷에서 지정한 위치의 클래스의 메소드가 실행되기 전에 현재 메소드 실행
    public void before(JoinPoint joinPoint) { // JoinPoint joinPoint : 위치나 시점, 특정 클래스, 특정 메소드라는 정보
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        System.out.println(joinPoint.getSignature());
        System.out.println(method.getName() + "메소드 실행 전");
    }

    @After("cut()") // 포인트 컷에서 지정한 위치의 클래스의 메소드가 실행된 후에 현재 메소드 실행
    public void after(JoinPoint joinPoint) { // JoinPoint joinPoint : 위치나 시점, 특정 클래스, 특정 메소드라는 정보
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        System.out.println(joinPoint.getSignature());
        System.out.println(method.getName() + "메소드 실행 후");
    }


}
