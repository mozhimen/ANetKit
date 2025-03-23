package com.mozhimen.netk.netty

import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.kotlin.utilk.commons.IUtilK
import com.mozhimen.netk.netty.commons.IConnectServerListener
import com.mozhimen.netk.netty.helpers.MessageDecoder
import com.mozhimen.netk.netty.helpers.MessageEncoder
import com.mozhimen.netk.netty.mos.Message
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import kotlin.concurrent.Volatile

/**
 * @ClassName NetKMettyServer
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/3/20
 * @Version 1.0
 */
class NetKNettyServer : IUtilK {
    /**
     * 服务是否已经启动
     */
    @Volatile
    private var _isServerStart = false

    /**
     * 引导类
     * 配置线程池，IP地址，端口号，Channel，业务Handler
     */
    private var _serverBootstrap: ServerBootstrap? = null
    private var _bossEventLoopGroup: EventLoopGroup? = null
    private var _workEventLoopGroup: EventLoopGroup? = null

    /**
     * 解码
     * 读取数据，处理拆包，粘包，压缩
     */
    private var _messageDecoder: MessageDecoder? = null

    /**
     * 编码
     */
    private var _messageEncoder: MessageEncoder? = null
    private var _simpleChannelInboundHandlerImpl: SimpleChannelInboundHandlerImpl? = null
    private var _iConnectServerListener: IConnectServerListener? = null
    private var _inetPort = 0
    private var _channelFuture: ChannelFuture? = null

    /////////////////////////////////////////////////////////////////////////////

    /**
     * 初始化
     */
    fun init(listener: IConnectServerListener?, inetPort: Int) {
        _inetPort = inetPort
        _iConnectServerListener = listener
        try {
            if (_isServerStart)
                return
            disconnect()
            _bossEventLoopGroup = NioEventLoopGroup()
            _workEventLoopGroup = NioEventLoopGroup()
            //构建引导程序
            _serverBootstrap = ServerBootstrap().apply {
                //设置EventGroup
                group(_bossEventLoopGroup, _workEventLoopGroup)
                //设置Channel
                channel(NioServerSocketChannel::class.java)
                option(ChannelOption.SO_BACKLOG, 128)
                //设置的好处是禁用Nagle算法。表示不延迟立即发送
                //这个算法试图减少TCP包的数量和结构性开销，将多个较小的包组合较大的包进行发送。
                //这个算法收TCP延迟确认影响，会导致相继两次向链接发送请求包。
                option(ChannelOption.TCP_NODELAY, false)
                option(ChannelOption.SO_KEEPALIVE, true)
                childHandler(CustomChannelInitializer())
            }
            _channelFuture = _serverBootstrap!!.bind(inetPort).sync()
            _isServerStart = _channelFuture!!.isSuccess
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkServer() {
        if (_isServerStart && !isServerOpen()) {
            disconnect()
            init(_iConnectServerListener, _inetPort)
        }
    }

    fun isServerOpen(): Boolean =
        _channelFuture != null && _channelFuture!!.channel() != null && _channelFuture!!.channel().isWritable

    /**
     * 写数据
     */
    fun writeMessage(`object`: Any?, channel: Channel?) {
        if (channel != null && channel.isOpen && channel.isActive) {
            channel.writeAndFlush(`object`).addListener {
                UtilKLogWrapper.d(TAG, "writeMessage: success")
            }
        }
    }

    /**
     * 关闭服务
     */
    fun disconnect() {
        try {
            _bossEventLoopGroup?.shutdownGracefully()
            _workEventLoopGroup?.shutdownGracefully()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _isServerStart = false
    }

    /**
     * 是否连接状态
     */
    fun isServerStart(): Boolean =
        _isServerStart

    /////////////////////////////////////////////////////////////////////////////

    inner class CustomChannelInitializer : ChannelInitializer<SocketChannel>() {
        override fun initChannel(socketChannel: SocketChannel) {
            _messageDecoder = MessageDecoder()
            _messageEncoder = MessageEncoder()
            _simpleChannelInboundHandlerImpl = SimpleChannelInboundHandlerImpl()
            socketChannel.pipeline().apply {
                addLast(_messageDecoder)
                addLast(_messageEncoder)
                addLast(_simpleChannelInboundHandlerImpl)
            }
        }
    }

    inner class SimpleChannelInboundHandlerImpl : SimpleChannelInboundHandler<Message>() {
        override fun channelRead0(channelHandlerContext: ChannelHandlerContext, message: Message) {
            //接收来自服务端的消息
            notifyReceiveMessage(channelHandlerContext.channel(), message)
        }

        /**
         * 连接成功
         */
        @Throws(Exception::class)
        override fun channelActive(ctx: ChannelHandlerContext) {
            super.channelActive(ctx)
            //链接成功
            notifyClientConnect(ctx.channel())
        }

        /**
         * 链接失败
         */
        @Throws(Exception::class)
        override fun channelInactive(ctx: ChannelHandlerContext) {
            super.channelInactive(ctx)
            //链接失败
            inActive(ctx)
            notifyClientDisConnect(ctx.channel())
        }

        /**
         * 异常
         */
        @Deprecated("Deprecated in Java")
        @Throws(Exception::class)
        override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
            super.exceptionCaught(ctx, cause)
            //异常处理
            inActive(ctx)
            UtilKLogWrapper.d(TAG, "exceptionCaught: cause.message ${cause.message}")
            cause.printStackTrace()
        }

        private fun inActive(ctx: ChannelHandlerContext) {
            try {
                val channel = ctx.channel()
                channel.pipeline().remove(_messageDecoder)
                channel.pipeline().remove(_simpleChannelInboundHandlerImpl)
                channel.pipeline().remove(_messageEncoder)
                channel.close()
                ctx.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////

    private fun notifyClientConnect(channel: Channel) {
        _iConnectServerListener?.onClientConnect(channel)
    }


    private fun notifyClientDisConnect(channel: Channel) {
        _iConnectServerListener?.onClientDisconnect(channel)
    }

    /**
     * 接收到一条消息
     */
    private fun notifyReceiveMessage(channel: Channel, message: Message) {
        _iConnectServerListener?.onClientReceiveMessage(channel, message)
    }
}
