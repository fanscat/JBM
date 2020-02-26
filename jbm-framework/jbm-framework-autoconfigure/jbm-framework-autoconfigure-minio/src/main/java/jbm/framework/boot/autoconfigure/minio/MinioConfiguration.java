/*
 * Copyright Jordan LEFEBURE © 2019.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package jbm.framework.boot.autoconfigure.minio;

import io.minio.MinioClient;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
@ConditionalOnClass(MinioClient.class)
@EnableConfigurationProperties(MinioConfigurationProperties.class)
@ComponentScan("jbm.framework.boot.autoconfigure.minio")
public class MinioConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MinioConfiguration.class);

    @Autowired
    private MinioConfigurationProperties minioConfigurationProperties;

    @Bean
    public MinioClient minioClient() throws InvalidEndpointException, InvalidPortException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, MinioException {

        MinioClient minioClient = null;
        try {
            minioClient = new MinioClient(
                    minioConfigurationProperties.getUrl(),
                    minioConfigurationProperties.getAccessKey(),
                    minioConfigurationProperties.getSecretKey(),
                    minioConfigurationProperties.isSecure()
            );
            minioClient.setTimeout(
                    minioConfigurationProperties.getConnectTimeout().toMillis(),
                    minioConfigurationProperties.getWriteTimeout().toMillis(),
                    minioConfigurationProperties.getReadTimeout().toMillis()
            );
        } catch (InvalidEndpointException | InvalidPortException e) {
            log.error("Error while connecting to Minio", e);
            throw e;
        }

        if (minioConfigurationProperties.isCheckBucket()) {
            try {
                log.debug("Checking if bucket {} exists", minioConfigurationProperties.getBucket());
                boolean b = minioClient.bucketExists(minioConfigurationProperties.getBucket());
                if (!b) {
                    if (minioConfigurationProperties.isCreateBucket()) {
                        try {
                            minioClient.makeBucket(minioConfigurationProperties.getBucket());
                        } catch (RegionConflictException e) {
                            throw new MinioException("Cannot create bucket", e);
                        }
                    } else {
                        throw new InvalidBucketNameException(minioConfigurationProperties.getBucket(), "Bucket does not exists");
                    }
                }
            } catch
            (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | XmlPullParserException | ErrorResponseException | InternalException | MinioException
                            e) {
                log.error("Error while checking bucket", e);
                throw e;
            }
        }

        return minioClient;
    }

}
