package org.example.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DiscardServer {

    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        /**
         * NioEventLoopGroup은 입출력 연산을 처리하는 멀티스레드 이벤트 루프입니다
         * 보스 그룹이 연결을 수락하고 수락된 연결을 작업자에게 등록하면 수락된 연결의 트래픽을 처리합니다.
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            /**
             * 새 서버 측 채널을 생성하고 들어오는 연결을 수락하는 클래스입니다. 주로 애플리케이션이 수행할 동작을 지정하거나 각종 설정을 지정합니다.
             * group(parent, child)을 통해 Acceptor(부모) 와 client(자식)에 대한 이벤트 루프 그릅을 설정합니다.
             * 이 이벤트 그룹은 서버채널과 채널의 모든 이벤트와 I/O를 처리하는데 사용됩니다.
             */
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    //채널 인스턴스 생성
                    .channel(NioServerSocketChannel.class)
                    //이벤트 루프에 등록한 채널을 쉽게 초기화할 수 있는 방법을 제공하는 특별한 채널 인바운드 핸들러입니다
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // initChannel()은 채널이 등록되면 호출됩니다. 메서드가 반환된 후 이 인스턴스는 채널의 채널파이프라인에서 제거됩니다.
                        // pipeline()은 채널에서 발생한 이벤트가 이동하는 통로이며, 이동 하는 이벤트를 처리하는 클래스 입니다.(채널 생성시 자동생성)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new DiscardServerHandler());
                        }
                    })
                    //동시에 수용할 클라이언트의 연결 요청 수를 설정한다.(클라이언트 수가 많더라도 동시접속할 확율상의 문제를 고려하여 설정한다)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // childOption()은 부모 서버채널이 수락하는 채널(이 경우 NioSocketChannel)을 위한 것입니다.
                    // SO_KEEPALIVE : 커널에서 상대방의 상태를 확인하는 패킷을 전송하는 옵션입니다.
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            /**
             * ChannelFuture
             * 비동기 채널 I/O의 작업 결과입니다. Netty의 모든 I/O 작업은 비동기식으로 모든 I/O 호출은 호출이 끝날 때 I/O 작업의 결과 또는
             * 상태에 대한 정보를 제공하는 ChannelFuture 인스턴스가 반환됩니다.
             *
             * 이때, 실패 및 취소 또한 완료 상태에 속합니다.
             *                                       +---------------------------+
             *                                       | Completed successfully    |
             *                                       +---------------------------+
             *                                  +---->      isDone() = true      |
             *  +--------------------------+    |    |   isSuccess() = true      |
             *  |        Uncompleted       |    |    +===========================+
             *  +--------------------------+    |    | Completed with failure    |
             *  |      isDone() = false    |    |    +---------------------------+
             *  |   isSuccess() = false    |----+---->      isDone() = true      |
             *  | isCancelled() = false    |    |    |       cause() = non-null  |
             *  |       cause() = null     |    |    +===========================+
             *  +--------------------------+    |    | Completed by cancellation |
             *                                  |    +---------------------------+
             *                                  +---->      isDone() = true      |
             *                                       | isCancelled() = true      |
             *                                       +---------------------------+
             */
            ChannelFuture future = bootstrap.bind(port).sync();
            //channle을 닫고 future를 닫으며, future가 완료될 때까지 기다렸다 실패하면 실패의 원인을 던집니다.
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8081;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new DiscardServer(port).run();
    }
}
