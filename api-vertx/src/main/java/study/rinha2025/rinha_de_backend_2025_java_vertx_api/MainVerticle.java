package study.rinha2025.rinha_de_backend_2025_java_vertx_api;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;

public class MainVerticle extends VerticleBase {

    @Override
    public Future<?> start() {
        return vertx.createHttpServer().requestHandler(req -> {
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x!");
        }).listen(8888).onSuccess(http -> {
            System.out.println("HTTP server started on port 8888");
        });
    }
}
