package com.miex.service;

import com.miex.util.Assert;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Map;

/**
 * @author liutz
 * @since 2022/3/8
 */
public class SrpcNettyService {
	private static ServerBootstrap server;

	public SrpcNettyService(Map<String,String> prop) {
		Assert.NotNull(prop.get("port"),"\"port\" can't be null");
		server = new ServerBootstrap();
		createBootStrap(prop);
	}

	private void createBootStrap(Map<String,String> prop) {
		EventLoopGroup bossGroup = new NioEventLoopGroup(Integer.parseInt(prop.get("parent_group")));
		EventLoopGroup workerGroup = new NioEventLoopGroup(Integer.parseInt(prop.get("child_group")));
		server.group(bossGroup,workerGroup)
				.channel(NioServerSocketChannel.class)
				.handler(new SrpcChannelInitializer())
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
	}
}
