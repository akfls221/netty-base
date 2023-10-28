package org.example.discard;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * ChannelInboundHandler
 * ChannelInboundHandler는 다양한 이벤트 핸들러 메서드를 제공합니다.
 */
public class DiscardServerSimpleHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println(
                "Client received: " + msg.toString(CharsetUtil.UTF_8));
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
