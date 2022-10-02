package org.laokou.gateway.config.gray.factory;

/**
 * @author Kou Shenhai
 */
//@Slf4j
//public class GrayGatewayFilterFactory extends AbstractGatewayFilterFactory<GrayGatewayFilterFactory.Config> {
//
//    private static final String GRAY_LB = "gateway-lb";
//    private final LoadBalancerClientFactory loadBalancerClientFactory;
//
//    public GrayGatewayFilterFactory(LoadBalancerClientFactory loadBalancerClientFactory) {
//        super(Config.class);
//        this.loadBalancerClientFactory = loadBalancerClientFactory;
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return ((exchange, chain) -> {
//            final Route route = getRoute(exchange);
//            final URI uri = route.getUri();
//            final String schemePrefix = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR);
//            if (null == uri || (!GRAY_LB.equals(uri.getScheme()) && !GRAY_LB.equals(schemePrefix))) {
//                return chain.filter(exchange);
//            }
//            ServerWebExchangeUtils.addOriginalRequestUrl(exchange,uri);
//            final HttpHeaders headers = exchange.getRequest().getHeaders();
//            final List<String> headerVersions = headers.get(GrayConstant.VERSION);
//            if (CollectionUtils.isEmpty(headerVersions)) {
//                throw new CustomException("缺少Gray关键字");
//            }
//            return null;
//        });
//    }
//
//    private Route getRoute(ServerWebExchange exchange) {
//        final Object attribute = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
//        if (attribute instanceof Route) {
//            return (Route) attribute;
//        }
//        throw new CustomException("丢失route");
//    }
//
//    @Setter
//    @Getter
//    public static class Config {
//        private String version;
//        private List<String> ips;
//    }
//
//}
