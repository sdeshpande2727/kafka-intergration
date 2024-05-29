package com.jupitertools.springtestkafka;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * A simple test which runs a Kafka instance in docker
 * and makes some actions with this instance.
 *
 * @author Anatoliy Korovin
 */
@KafkaTestContainer
@SpringBootTest
class KafkaTestContainerTest {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Test
	void sendAndReceiveTest() throws InterruptedException {
		// Arrange
		assertThat(kafkaTemplate).isNotNull();
		kafkaTemplate.send("test-topic-1", "flight of a dragon");
		// Wait
		Awaitility.await()
		          .atMost(5, TimeUnit.SECONDS)
		          .until(() -> TestConfig.invocations.size() > 0);
		// Assert
		assertThat(TestConfig.invocations).hasSize(1)
		                                  .contains("flight of a dragon");
	}

	@TestConfiguration
	public static class TestConfig {

		static List<String> invocations = new ArrayList<>();

		@KafkaListener(topics = "test-topic-1", groupId = "test-group")
		public void onKafkaEvent(String message) {
			invocations.add(message);
		}
	}

}
