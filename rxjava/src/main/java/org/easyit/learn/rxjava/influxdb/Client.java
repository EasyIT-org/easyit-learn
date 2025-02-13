package org.easyit.learn.rxjava.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        //    bucket: wattsonic
        //    org: Wattsonic
        //    token: cXOzmB3f_JUKwmN3vVnvfMnVphs2OeDBaSdaJW-hHY9DsSLrmxj6iwUYnSF3AEouQo3TPB-lcVI7jDenGz82ew==
        //    url: http://121.41.40.228:8086
        String url = "http://121.41.40.228:8086";
        String token = "cXOzmB3f_JUKwmN3vVnvfMnVphs2OeDBaSdaJW-hHY9DsSLrmxj6iwUYnSF3AEouQo3TPB-lcVI7jDenGz82ew==";
        String measurement = "test";
        String bucket = "wattsonic";
        String org = "Wattsonic";

        InfluxDBClient client = InfluxDBClientFactory.create(url, token.toCharArray());
        WriteApi writeApi = client.makeWriteApi();

        int totalCount = 1;
        for (int i = 0; i < totalCount; i++) {
            long time = System.currentTimeMillis();
            Point point = Point.measurement(measurement)
                               .time(time, WritePrecision.MS);
            point.addField("count",i);
            writeApi.writePoint(bucket,org,point);
            Thread.sleep(1);
        }

        Thread.sleep(2000);

    }


}
