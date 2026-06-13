package edu.rutmiit.demo.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Сервисы
 * Unary RPC - один запрос это один ответ (типа аналог REST GET/POST)
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.66.0)",
    comments = "Source: plant_analytics.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class GreenhouseAnalyticsGrpc {

  private GreenhouseAnalyticsGrpc() {}

  public static final java.lang.String SERVICE_NAME = "plantanalytics.GreenhouseAnalytics";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<edu.rutmiit.demo.grpc.AnalyzeGreenhouseRequest,
      edu.rutmiit.demo.grpc.GreenhouseAnalysisResponse> getAnalyzeGreenhouseMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AnalyzeGreenhouse",
      requestType = edu.rutmiit.demo.grpc.AnalyzeGreenhouseRequest.class,
      responseType = edu.rutmiit.demo.grpc.GreenhouseAnalysisResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<edu.rutmiit.demo.grpc.AnalyzeGreenhouseRequest,
      edu.rutmiit.demo.grpc.GreenhouseAnalysisResponse> getAnalyzeGreenhouseMethod() {
    io.grpc.MethodDescriptor<edu.rutmiit.demo.grpc.AnalyzeGreenhouseRequest, edu.rutmiit.demo.grpc.GreenhouseAnalysisResponse> getAnalyzeGreenhouseMethod;
    if ((getAnalyzeGreenhouseMethod = GreenhouseAnalyticsGrpc.getAnalyzeGreenhouseMethod) == null) {
      synchronized (GreenhouseAnalyticsGrpc.class) {
        if ((getAnalyzeGreenhouseMethod = GreenhouseAnalyticsGrpc.getAnalyzeGreenhouseMethod) == null) {
          GreenhouseAnalyticsGrpc.getAnalyzeGreenhouseMethod = getAnalyzeGreenhouseMethod =
              io.grpc.MethodDescriptor.<edu.rutmiit.demo.grpc.AnalyzeGreenhouseRequest, edu.rutmiit.demo.grpc.GreenhouseAnalysisResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AnalyzeGreenhouse"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.rutmiit.demo.grpc.AnalyzeGreenhouseRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.rutmiit.demo.grpc.GreenhouseAnalysisResponse.getDefaultInstance()))
              .setSchemaDescriptor(new GreenhouseAnalyticsMethodDescriptorSupplier("AnalyzeGreenhouse"))
              .build();
        }
      }
    }
    return getAnalyzeGreenhouseMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GreenhouseAnalyticsStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GreenhouseAnalyticsStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GreenhouseAnalyticsStub>() {
        @java.lang.Override
        public GreenhouseAnalyticsStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GreenhouseAnalyticsStub(channel, callOptions);
        }
      };
    return GreenhouseAnalyticsStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GreenhouseAnalyticsBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GreenhouseAnalyticsBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GreenhouseAnalyticsBlockingStub>() {
        @java.lang.Override
        public GreenhouseAnalyticsBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GreenhouseAnalyticsBlockingStub(channel, callOptions);
        }
      };
    return GreenhouseAnalyticsBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GreenhouseAnalyticsFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GreenhouseAnalyticsFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GreenhouseAnalyticsFutureStub>() {
        @java.lang.Override
        public GreenhouseAnalyticsFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GreenhouseAnalyticsFutureStub(channel, callOptions);
        }
      };
    return GreenhouseAnalyticsFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Сервисы
   * Unary RPC - один запрос это один ответ (типа аналог REST GET/POST)
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Анализирует теплицу и возвращает вычисленные метрики
     * </pre>
     */
    default void analyzeGreenhouse(edu.rutmiit.demo.grpc.AnalyzeGreenhouseRequest request,
        io.grpc.stub.StreamObserver<edu.rutmiit.demo.grpc.GreenhouseAnalysisResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAnalyzeGreenhouseMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service GreenhouseAnalytics.
   * <pre>
   * Сервисы
   * Unary RPC - один запрос это один ответ (типа аналог REST GET/POST)
   * </pre>
   */
  public static abstract class GreenhouseAnalyticsImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return GreenhouseAnalyticsGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service GreenhouseAnalytics.
   * <pre>
   * Сервисы
   * Unary RPC - один запрос это один ответ (типа аналог REST GET/POST)
   * </pre>
   */
  public static final class GreenhouseAnalyticsStub
      extends io.grpc.stub.AbstractAsyncStub<GreenhouseAnalyticsStub> {
    private GreenhouseAnalyticsStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GreenhouseAnalyticsStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GreenhouseAnalyticsStub(channel, callOptions);
    }

    /**
     * <pre>
     * Анализирует теплицу и возвращает вычисленные метрики
     * </pre>
     */
    public void analyzeGreenhouse(edu.rutmiit.demo.grpc.AnalyzeGreenhouseRequest request,
        io.grpc.stub.StreamObserver<edu.rutmiit.demo.grpc.GreenhouseAnalysisResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAnalyzeGreenhouseMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service GreenhouseAnalytics.
   * <pre>
   * Сервисы
   * Unary RPC - один запрос это один ответ (типа аналог REST GET/POST)
   * </pre>
   */
  public static final class GreenhouseAnalyticsBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<GreenhouseAnalyticsBlockingStub> {
    private GreenhouseAnalyticsBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GreenhouseAnalyticsBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GreenhouseAnalyticsBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Анализирует теплицу и возвращает вычисленные метрики
     * </pre>
     */
    public edu.rutmiit.demo.grpc.GreenhouseAnalysisResponse analyzeGreenhouse(edu.rutmiit.demo.grpc.AnalyzeGreenhouseRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAnalyzeGreenhouseMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service GreenhouseAnalytics.
   * <pre>
   * Сервисы
   * Unary RPC - один запрос это один ответ (типа аналог REST GET/POST)
   * </pre>
   */
  public static final class GreenhouseAnalyticsFutureStub
      extends io.grpc.stub.AbstractFutureStub<GreenhouseAnalyticsFutureStub> {
    private GreenhouseAnalyticsFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GreenhouseAnalyticsFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GreenhouseAnalyticsFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Анализирует теплицу и возвращает вычисленные метрики
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<edu.rutmiit.demo.grpc.GreenhouseAnalysisResponse> analyzeGreenhouse(
        edu.rutmiit.demo.grpc.AnalyzeGreenhouseRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAnalyzeGreenhouseMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ANALYZE_GREENHOUSE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ANALYZE_GREENHOUSE:
          serviceImpl.analyzeGreenhouse((edu.rutmiit.demo.grpc.AnalyzeGreenhouseRequest) request,
              (io.grpc.stub.StreamObserver<edu.rutmiit.demo.grpc.GreenhouseAnalysisResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getAnalyzeGreenhouseMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              edu.rutmiit.demo.grpc.AnalyzeGreenhouseRequest,
              edu.rutmiit.demo.grpc.GreenhouseAnalysisResponse>(
                service, METHODID_ANALYZE_GREENHOUSE)))
        .build();
  }

  private static abstract class GreenhouseAnalyticsBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GreenhouseAnalyticsBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return edu.rutmiit.demo.grpc.PlantAnalytics.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GreenhouseAnalytics");
    }
  }

  private static final class GreenhouseAnalyticsFileDescriptorSupplier
      extends GreenhouseAnalyticsBaseDescriptorSupplier {
    GreenhouseAnalyticsFileDescriptorSupplier() {}
  }

  private static final class GreenhouseAnalyticsMethodDescriptorSupplier
      extends GreenhouseAnalyticsBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    GreenhouseAnalyticsMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GreenhouseAnalyticsGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GreenhouseAnalyticsFileDescriptorSupplier())
              .addMethod(getAnalyzeGreenhouseMethod())
              .build();
        }
      }
    }
    return result;
  }
}
