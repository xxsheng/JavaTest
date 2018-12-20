package netty20181220;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerAppender;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * 服务端处理通道。这里只是打印一下请求的内容，并不对请求进行任何的回应 DiscardServerHandler继承自
 * ChannelHandlerAdapter，这个类实现了ChannelHandler接口，ChannelHandler提供了许多事件处理的接口方法，
 * 然后你可以覆盖这些方法，现在仅仅只需要继承ChannelHandlerAdapter类而不是自己去实现接口方法。
 * @author Olympus_Pactera
 *
 */
public class DiscardServerHandler extends ChannelHandlerAppender {

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
	 * 发生异常的时候触发这个方法 
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		cause.printStackTrace();
		ctx.close();
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 * 这里我们覆盖了channelRead()事件处理方法，每当从客户端收到新的数据时，这个方法会在收到消息时被调用，
	 * 这个例子中，收到的消息的类型是ByteBuf 
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		try {
			System.out.println("接收到的信息-----");
			ByteBuf in = (ByteBuf) msg;
			//打印客户端输入，传输过来的字符
			System.out.println(in.toString(CharsetUtil.UTF_8));
		} finally {
			// TODO: handle finally clause
			ReferenceCountUtil.release(msg);
		}
			
	}

	
}
