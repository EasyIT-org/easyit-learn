package org.easyit.learn.asm.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import java.util.function.Consumer;

public class ByteBufUtils {
    private static final ByteBufAllocator BUF_ALLOCATOR = new PooledByteBufAllocator(false);

    public static ByteBuf buf() {
        return BUF_ALLOCATOR.buffer();
    }

    public static void allocateAndFree(Consumer<ByteBuf> c) {
        ByteBuf buffer = BUF_ALLOCATOR.buffer();
        try {
            c.accept(buffer);
        } finally {
            release(buffer);
        }
    }

    public static ByteBuf emptyByteBuf() {
        return BUF_ALLOCATOR.buffer(0, 0);
    }

    public static ByteBuf buf(byte[] body) {
        if (body == null || body.length == 0) {
            return buf();
        }
        ByteBuf buffer = BUF_ALLOCATOR.buffer(body.length);
        buffer.writeBytes(body);
        return buffer;
    }

    public static void release(ByteBuf bytebuf) {
        if (bytebuf == null) {
            throw new NullPointerException();
        }
        bytebuf.release();
    }

    public static CompositeByteBuf compositeBuffer() {
        return BUF_ALLOCATOR.compositeBuffer();
    }

    public static byte[] toArray(ByteBuf buf) {
        int i = buf.readableBytes();
        byte[] bytes = new byte[i];
        buf.readBytes(bytes);
        return bytes;
    }

    public static ByteBuf toBuf(byte[] byteArrayOf) {
        ByteBuf buf = buf();
        buf.writeBytes(byteArrayOf);
        return buf;
    }
}

