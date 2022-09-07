package viamcp.utils;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import viamcp.handler.CommonTransformer;

public class NettyUtil {
    public static ChannelPipeline decodeEncodePlacement(ChannelPipeline instance, String base, String newHandler, ChannelHandler handler) {
        switch (base) {
            case "decoder": {
                if (instance.get(CommonTransformer.HANDLER_DECODER_NAME) != null) {
                    base = CommonTransformer.HANDLER_DECODER_NAME;
                }

                break;
            }
            case "encoder": {
                if (instance.get(CommonTransformer.HANDLER_ENCODER_NAME) != null) {
                    base = CommonTransformer.HANDLER_ENCODER_NAME;
                }

                break;
            }
        }

        return instance.addBefore(base, newHandler, handler);
    }
}
