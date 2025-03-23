package com.mozhimen.netk.netty

import com.mozhimen.kotlin.utilk.android.os.UtilKHandlerWrapper
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.kotlin.utilk.commons.IUtilK
import com.mozhimen.netk.netty.commons.IConnectClientListener
import com.mozhimen.netk.netty.helpers.MessageDecoder
import com.mozhimen.netk.netty.helpers.MessageEncoder
import com.mozhimen.netk.netty.mos.Message
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent
import io.netty.handler.timeout.IdleStateHandler
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.FutureListener
import java.util.concurrent.TimeUnit
import kotlin.concurrent.Volatile

/**
 * @ClassName NetKNettyClient
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/3/13
 * @Version 1.0
 */
class NetKNettyClient : IUtilK {
    companion object {
        @JvmStatic
        val instance = INSTANCE.holder

        private const val TIME_DELAY_CONNECT: Long = 3000
        private const val RECONNECT_TIMEOUT = 3
    }

    private object INSTANCE {
        val holder = NetKNettyClient()
    }

    enum class EConnectState {
        CONNECTED,
        CONNECTING,
        DISCONNECTED,
        CONNECT_FAIL,
        IDLE
    }

    ////////////////////////////////////////////////////////////////////////

    @Volatile
    private var _connectState = EConnectState.IDLE

    /**
     * 负责管理EventLoop
     * 集成自 ExecutorService 可以理解为线程池
     */
    private var _eventLoopGroup: EventLoopGroup? = null

    /**
     * 引导类
     * 配置线程池，IP地址，端口号，Channel，业务Handler
     */
    private var _bootstrap: Bootstrap? = null
    private var _channel: Channel? = null
    private var _channelHandlerContext: ChannelHandlerContext? = null

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
    private var _idleStateHandler: IdleStateHandler? = null
    private var _connectionClientListener: IConnectClientListener? = null
    private var _reconnectIndex = 0
    private val _reconnectTask: Runnable = Runnable {
        connect()
    }

    /////////////////////////////////////////////////////////////////////////////

    /**
     * 初始化
     */
    fun init(ip: String, port: Int, listener: IConnectClientListener) {
        _reconnectIndex = 0
        _connectionClientListener = listener
        try {
            if (_connectState == EConnectState.CONNECTED) {
                notifyConnect()
                return
            }
            disconnect()
            //构建线程池
            _eventLoopGroup = NioEventLoopGroup()
            //构建引导程序
            _bootstrap = Bootstrap().apply {
                //设置EventGroup
                group(_eventLoopGroup)
                //设置Channel
                channel(NioSocketChannel::class.java)
                //设置的好处是禁用Nagle算法。表示不延迟立即发送
                //这个算法试图减少TCP包的数量和结构性开销，将多个较小的包组合较大的包进行发送。
                //这个算法收TCP延迟确认影响，会导致相继两次向链接发送请求包。
                option(ChannelOption.TCP_NODELAY, false)
                option(ChannelOption.SO_KEEPALIVE, true)
                remoteAddress(ip, port)
                handler(CustomChannelInitializer())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 连接
     */
    fun connect() {
        try {
            _connectionClientListener?.onServerConnectStart()
            if (_bootstrap == null || _connectState == EConnectState.CONNECTED || _connectState == EConnectState.CONNECTING) {
                return
            }
            _connectState = EConnectState.CONNECTING
            val channelFuture = _bootstrap!!.connect()
            channelFuture.addListener(object : FutureListener<Any> {
                override fun operationComplete(future: Future<Any>?) {
                    val isSuccess = future?.isSuccess
                    if (isSuccess == true) {
                        _channel = channelFuture.channel()
                        _connectState = EConnectState.CONNECTED
                    } else {
                        _connectState = EConnectState.CONNECT_FAIL
                        _reconnectIndex++
                        if (_reconnectIndex > RECONNECT_TIMEOUT) {
                            notifyConnectFail()
                            return
                        }
                        UtilKHandlerWrapper.removeCallbacks(_reconnectTask)
                        UtilKHandlerWrapper.postDelayed(TIME_DELAY_CONNECT, _reconnectTask)
                    }
                }
            })
        } catch (e: Exception) {
            _connectState = EConnectState.DISCONNECTED
            e.printStackTrace()
        }
    }

    /**
     * 写数据
     */
    fun writeMessage(obj: Any) {
        if (_channel != null && _channel!!.isOpen && _channel!!.isActive) {
            _channel!!.writeAndFlush(obj).addListener {
                UtilKLogWrapper.d(TAG, "writeMessage: success " + (obj as Message).body)
            }
        }
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        _connectState = EConnectState.DISCONNECTED
        try {
            _eventLoopGroup?.shutdownGracefully()
            _channel?.close()
            _channelHandlerContext?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 是否连接状态
     */
    fun isConnect(): Boolean =
        _connectState == EConnectState.CONNECTED && _channel!!.isActive

    //////////////////////////////////////////////////////////////////////////////////////

    inner class CustomChannelInitializer : ChannelInitializer<SocketChannel>() {
        override fun initChannel(socketChannel: SocketChannel) {
            _messageDecoder = MessageDecoder()
            _messageEncoder = MessageEncoder()
            _simpleChannelInboundHandlerImpl = SimpleChannelInboundHandlerImpl()
            _idleStateHandler = IdleStateHandler(0, 90, 0, TimeUnit.SECONDS)
            socketChannel.pipeline().apply {
                addLast(_idleStateHandler)
                addLast(_messageDecoder)
                addLast(_messageEncoder)
                addLast(_simpleChannelInboundHandlerImpl)
            }
        }
    }

    inner class SimpleChannelInboundHandlerImpl : SimpleChannelInboundHandler<Message>() {
        override fun channelRead0(channelHandlerContext: ChannelHandlerContext, message: Message) {
            //接收来自服务端的消息
            notifyReceiveMessage(message)
        }

        /**
         * 连接成功
         */
        @Throws(Exception::class)
        override fun channelActive(ctx: ChannelHandlerContext) {
            super.channelActive(ctx)
            //链接成功
            _connectState = EConnectState.CONNECTED
            notifyConnect()
            _channelHandlerContext = ctx
        }

        /**
         * 链接失败
         */
        @Throws(Exception::class)
        override fun channelInactive(ctx: ChannelHandlerContext) {
            super.channelInactive(ctx)
            //链接失败
            inActive(ctx)
            _connectState = EConnectState.DISCONNECTED
            notifyDisconnect()
            if (/*!disConnected*/_connectState != EConnectState.DISCONNECTED) {
                notifyReInit()
            }
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
            cause.printStackTrace()
            UtilKLogWrapper.d(TAG, "exceptionCaught: ${cause.message}")
            _connectState = EConnectState.DISCONNECTED
            notifyDisconnect()
            notifyReInit()
        }


        @Throws(Exception::class)
        override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
            super.userEventTriggered(ctx, evt)
            if (evt is IdleStateEvent) {
                when (evt.state()) {
                    IdleState.WRITER_IDLE -> {}
                    else -> {}
                }
            }
        }

        private fun inActive(ctx: ChannelHandlerContext) {
            try {
                _channel?.pipeline()?.apply {
                    remove(_messageDecoder)
                    remove(_simpleChannelInboundHandlerImpl)
                    remove(_messageEncoder)
                    remove(_idleStateHandler)
                }
                ctx.channel().close()
                ctx.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////

    private fun notifyConnectFail() {
        runOnUi {
            _connectionClientListener?.onServerConnectFail()
        }
    }

    /**
     * 通过建立TCP连接
     */
    private fun notifyConnect() {
        runOnUi {
            _reconnectIndex = 0
            _connectionClientListener?.onServerConnect()
        }
    }

    /**
     * 通知TCP连接断开
     */
    private fun notifyDisconnect() {
        runOnUi {
            _reconnectIndex = 0
            _connectionClientListener?.onServerDisconnect()
        }
    }

    /**
     * 通过主进程接收到一条消息
     */
    private fun notifyReceiveMessage(message: Message) {
        runOnUi {
            _connectionClientListener?.onServerReceiveMessage(message)
        }
    }

    /**
     * 通知重新连接
     */
    private fun notifyReInit() {
        runOnUiDelay(TIME_DELAY_CONNECT) {
            _connectionClientListener?.onServerReconnect()
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    private fun runOnUi(runnable: Runnable) {
        UtilKHandlerWrapper.post(runnable)
    }

    private fun runOnUiDelay(delay: Long, runnable: Runnable) {
        UtilKHandlerWrapper.postDelayed(delay, runnable)
    }
}
