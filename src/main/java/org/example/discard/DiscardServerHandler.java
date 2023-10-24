package org.example.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * ChannelInboundHandler
 * ChannelInboundHandler는 다양한 이벤트 핸들러 메서드를 제공합니다.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 클라이언트로 부터 새 데이터가 수신될 때마다 수신된 메시지와 함께 호출됩니다. 이 예제에서 수신된 메시지의 유형은 ByteBuf입니다.
     * Discard(읽고버리는) 프로토콜 구현을 위해선 핸들러가 수신된 메시지를 무시해야 합니다.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            // Do something with msg
        } finally {
            //이 부분에서 왜 Netty는 try-with-resource문을 사용하지 않을까 의문이었고 결론적으로 Netty 4.1은 대부분 Java 6에 대해 컴파일 되어서임을 알게되었다.
            //https://github.com/netty/netty/issues/9498
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * exceptionCaught()핸들러 메소드의 경우 I/O 오류로 인해 Netty에서 예외가 발생하거나, 이벤트를 처리하는 동안 던져진 예외로 인해
     * 핸들러 구현에서 예외가 발생하면 Throwable과 함께 호출됩니다.
     * 대부분의 경우 예외는 여기에 기록되고 관련 채널이 닫혀야 합니다. 예를 들어 연결을 닫기 전 오류 코드가 포함된 응답 메시지를 보낼 수 있습니다.
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
