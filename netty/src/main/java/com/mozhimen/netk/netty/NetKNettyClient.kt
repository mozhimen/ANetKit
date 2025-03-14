package com.mozhimen.netk.netty

import android.util.Log
import com.mozhimen.netk.netty.commons.IConnectClientListener
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
class ConnectionClient private constructor() {
    //是否已连接
    @Volatile
    private var isConnected = false
    //正在连接
    @Volatile
    private var connecting = false
    //主动断开状态
    @Volatile
    private var disConnected = false
    //是否连接失败
    @Volatile
    private var connectFailed = false

    /**
     * 负责管理EventLoop
     * 集成自 ExecutorService 可以理解为线程池
     */
    private var mGroup: EventLoopGroup? = null

    /**
     * 引导类
     * 配置线程池，IP地址，端口号，Channel，业务Handler
     */
    private var mBootstrap: Bootstrap? = null
    private var mChannel: Channel? = null
    private var mChannelHandlerContext: ChannelHandlerContext? = null

    /**
     * 解码
     * 读取数据，处理拆包，粘包，压缩
     */
    private var mDemoDecoder: DemoDecoder? = null

    /**
     * 编码
     */
    private var mDemoEncoder: DemoEncoder? = null

    private var mCustomHandler: CustomHandler? = null

    private var mIdleStateHandler: IdleStateHandler? = null

    var connectionClientListener: IConnectClientListener? = null

    private var reconnectIndex = 0
    private val mReconnectTask = Runnable {
        connect()
    }

    /**
     * 初始化
     */
    fun init(ip: String, port: Int, listener: IConnectClientListener?) {
        reconnectIndex = 0
        this.connectionClientListener = listener
        this.disConnected = false
        try {
            if (isConnected) {
                notifyConnect()
                return
            }
            if (mGroup != null) {
                mGroup!!.shutdownGracefully()
            }
            disConnect(false)
            //构建线程池
            mGroup = NioEventLoopGroup()
            //构建引导程序
            mBootstrap = Bootstrap()
            //设置EventGroup
            mBootstrap!!.group(mGroup)
            //设置Channel
            mBootstrap!!.channel(NioSocketChannel::class.java)
            //设置的好处是禁用Nagle算法。表示不延迟立即发送
            //这个算法试图减少TCP包的数量和结构性开销，将多个较小的包组合较大的包进行发送。
            //这个算法收TCP延迟确认影响，会导致相继两次向链接发送请求包。
            mBootstrap!!.option(ChannelOption.TCP_NODELAY, false)
            mBootstrap!!.option(ChannelOption.SO_KEEPALIVE, true)
            mBootstrap!!.remoteAddress(ip, port)
            mBootstrap!!.handler(CustomChannelInitializer())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 连接
     */
    fun connect() {
        try {
            if (connectionClientListener != null) {
                connectionClientListener!!.onServerConnectStart()
            }
            if (mBootstrap == null || isConnected || connecting) {
                return
            }
            connectFailed = false
            connecting = true
            val channelFuture = mBootstrap!!.connect()
            channelFuture.addListener(object : FutureListener<Any?> {
                override fun operationComplete(future: Future<*>) {
                    connecting = false
                    val isSuccess = future.isSuccess
                    if (isSuccess) {
                        mChannel = channelFuture.channel()
                        disConnected = false
                        isConnected = true
                        connectFailed = false
                    } else {
                        isConnected = false
                        connectFailed = true
                        reconnectIndex++
                        if (reconnectIndex > RECONNECT_TIMEOUT) {
                            notifyConnectFailure()
                            return
                        }
                        UIScheduler.getUIScheduler().removeRunnable(mReconnectTask)
                        UIScheduler.getUIScheduler().postRunnableDelayed(mReconnectTask, TIME_DELAY_CONNECT)
                    }
                }
            })
        } catch (e: Exception) {
            connecting = false
            e.printStackTrace()
        }
    }

    /**
     * 写数据
     *
     * @param object Object
     */
    fun writeMessage(`object`: Any) {
        if (mChannel != null && mChannel!!.isOpen && mChannel!!.isActive) {
            mChannel!!.writeAndFlush(`object`).addListener {
                val message = `object` as Message
                writeLog("writeMessage success " + message.body)
            }
        }
    }

    /**
     * 断开连接
     *
     * @param onPurpose true 主动
     */
    fun disConnect(onPurpose: Boolean) {
        disConnected = onPurpose
        isConnected = false
        connecting = false
        try {
            if (mGroup != null) {
                mGroup!!.shutdownGracefully()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (mChannel != null) {
                mChannel!!.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (mChannelHandlerContext != null) {
                mChannelHandlerContext!!.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeHandler() {
        try {
            mChannel!!.pipeline().remove(mDemoDecoder)
            mChannel!!.pipeline().remove(mCustomHandler)
            mChannel!!.pipeline().remove(mDemoEncoder)
            mChannel!!.pipeline().remove(mIdleStateHandler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 是否连接状态
     *
     * @return true 连接
     */
    fun isConnected(): Boolean {
        return isConnected && mChannel!!.isActive
    }

    inner class CustomChannelInitializer : ChannelInitializer<SocketChannel>() {
        override fun initChannel(socketChannel: SocketChannel) {
            val pipeline = socketChannel.pipeline()
            mDemoDecoder = DemoDecoder()
            mDemoEncoder = DemoEncoder()
            mCustomHandler = CustomHandler()
            mIdleStateHandler = IdleStateHandler(0, 90, 0, TimeUnit.SECONDS)
            pipeline.addLast(mIdleStateHandler)
            pipeline.addLast(mDemoDecoder)
            pipeline.addLast(mDemoEncoder)
            pipeline.addLast(mCustomHandler)
        }

        @Throws(Exception::class)
        override fun handlerAdded(ctx: ChannelHandlerContext) {
            super.handlerAdded(ctx)
        }

        @Throws(Exception::class)
        override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
            super.exceptionCaught(ctx, cause)
        }
    }

    inner class CustomHandler : SimpleChannelInboundHandler<Message>() {
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
            isConnected = true
            notifyConnect()
            mChannelHandlerContext = ctx
        }

        /**
         * 链接失败
         */
        @Throws(Exception::class)
        override fun channelInactive(ctx: ChannelHandlerContext) {
            super.channelInactive(ctx)
            //链接失败
            inActive(ctx)
            isConnected = false
            connecting = false
            notifyDisConnect()
            if (!disConnected) {
                notifyReInit()
            }
        }

        /**
         * 异常
         */
        @Throws(Exception::class)
        override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
            super.exceptionCaught(ctx, cause)
            //异常处理
            inActive(ctx)
            if (cause != null) {
                writeLog(cause.message)
                cause.printStackTrace()
            }
            isConnected = false
            connecting = false
            notifyDisConnect()
            notifyReInit()
        }

        @Throws(Exception::class)
        override fun handlerAdded(ctx: ChannelHandlerContext) {
            super.handlerAdded(ctx)
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

        @Throws(Exception::class)
        override fun channelWritabilityChanged(ctx: ChannelHandlerContext) {
            super.channelWritabilityChanged(ctx)
        }
    }

    private fun inActive(ctx: ChannelHandlerContext) {
        removeHandler()
        try {
            val channel = ctx.channel()
            channel.close()
            ctx.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun notifyConnectFailure() {
        runOnUi {
            if (connectionClientListener != null) {
                connectionClientListener!!.onServerConnectFail()
            }
        }
    }

    /**
     * 通过建立TCP连接
     */
    private fun notifyConnect() {
        runOnUi {
            reconnectIndex = 0
            if (connectionClientListener != null) {
                connectionClientListener!!.onServerConnect()
            }
        }
    }

    /**
     * 通知TCP连接断开
     */
    private fun notifyDisConnect() {
        runOnUi {
            reconnectIndex = 0
            if (connectionClientListener != null) {
                connectionClientListener!!.onServerDisconnect(disConnected)
            }
        }
    }

    /**
     * 通过主进程接收到一条消息
     *
     * @param message 消息
     */
    private fun notifyReceiveMessage(message: Message) {
        runOnUi {
            if (connectionClientListener != null) {
                connectionClientListener!!.onServerReceiveMessage(message)
            }
        }
    }

    /**
     * 通知重新连接
     */
    private fun notifyReInit() {
        runOnUiDelay({
            if (connectionClientListener != null) {
                connectionClientListener!!.onServerReconnect()
            }
        }, TIME_DELAY_CONNECT)
    }

    private fun runOnUi(runnable: Runnable) {
        UIScheduler.getUIScheduler().postRunnable(runnable)
    }

    private fun runOnUiDelay(runnable: Runnable, delay: Long) {
        UIScheduler.getUIScheduler().postRunnableDelayed(runnable, delay)
    }

    private fun writeLog(info: String?) {
        Log.d(TAG, info!!)
    }

    companion object {
        private const val TAG = "netty"

        private const val TIME_DELAY_CONNECT: Long = 3000

        private const val RECONNECT_TIMEOUT = 3

        /**
         * 获取引擎
         */
        val connectionClient: ConnectionClient = ConnectionClient()
    }
}
