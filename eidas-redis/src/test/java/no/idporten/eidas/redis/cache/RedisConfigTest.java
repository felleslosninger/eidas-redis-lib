package no.idporten.eidas.redis.cache;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import static org.junit.Assert.*;


public class RedisConfigTest {

    private RedisConfig config;


    @Before
    public void setUp() {
        config = new RedisConfig();
        config.setRedisHost("localhost");
        config.setRedisPort("6379");
        config.setRedisPassword("password");
    }

    @Test
    public void testStandaloneRedisConnectionFactory() {
        config.setSentinelNodes(null);
        LettuceConnectionFactory factory = config.redisConnectionFactory();
        assertNotNull(factory);
        assertEquals("localhost", factory.getHostName());
        assertEquals(6379, factory.getPort());
        assertFalse(factory.isRedisSentinelAware());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStandaloneRedisConnectionFactoryMissingHost() {
        config.setRedisHost(null);
        config.redisConnectionFactory();
    }

    @Test
    public void testSentinelRedisConnectionFactory() {
        config.setSentinelNodes("node1:26379");
        config.setSentinelMaster("mymaster");
        LettuceConnectionFactory factory = config.redisConnectionFactory();
        assertNotNull(factory);
        assertTrue(factory.isRedisSentinelAware());
    }

    @Test
    public void testMultipleSentinelRedisConnectionFactory() {
        config.setSentinelNodes("node1:26379,node2:26379");
        config.setSentinelMaster("mymaster");
        LettuceConnectionFactory factory = config.redisConnectionFactory();
        assertNotNull(factory);
        assertTrue(factory.isRedisSentinelAware());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSentinelRedisConnectionFactoryInvalidNode() {
        config.setSentinelNodes("node1:26379,invalid_node");
        config.redisConnectionFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSentinelRedisConnectionFactoryMissingPassword() {
        config.setSentinelNodes("node1:26379,node2:26379");
        config.setSentinelMaster("mymaster");
        config.setRedisPassword(null);
        config.redisConnectionFactory();
    }
}
