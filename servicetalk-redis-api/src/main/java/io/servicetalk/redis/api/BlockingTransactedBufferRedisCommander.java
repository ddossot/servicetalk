/*
 * Copyright © 2018 Apple Inc. and the ServiceTalk project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.servicetalk.redis.api;

import io.servicetalk.buffer.api.Buffer;

import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nullable;

/**
 * Redis transacted command client that uses {@link Buffer} for keys and data.
 * <p>
 * Note that Redis Simple String responses are always returned as {@link String}. This API is provided for convenience
 * for a more familiar sequential programming model.
 */
@Generated({})
public abstract class BlockingTransactedBufferRedisCommander implements AutoCloseable {

    /**
     * {@inheritDoc}
     * <p>
     * This will close the underlying {@link RedisRequester}!
     */
    @Override
    public abstract void close() throws Exception;

    /**
     * Append a value to a key.
     *
     * @param key the key
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.APPEND)
    public abstract String append(@RedisProtocolSupport.Key Buffer key, Buffer value) throws Exception;

    /**
     * Authenticate to the server.
     *
     * @param password the password
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.AUTH)
    public abstract String auth(Buffer password) throws Exception;

    /**
     * Asynchronously rewrite the append-only file.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BGREWRITEAOF)
    public abstract String bgrewriteaof() throws Exception;

    /**
     * Asynchronously save the dataset to disk.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BGSAVE)
    public abstract String bgsave() throws Exception;

    /**
     * Count set bits in a string.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BITCOUNT)
    public abstract String bitcount(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Count set bits in a string.
     *
     * @param key the key
     * @param start the start
     * @param end the end
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BITCOUNT)
    public abstract String bitcount(@RedisProtocolSupport.Key Buffer key, @Nullable Long start,
                                    @Nullable Long end) throws Exception;

    /**
     * Perform arbitrary bitfield integer operations on strings.
     *
     * @param key the key
     * @param operations the operations
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BITFIELD)
    public abstract String bitfield(@RedisProtocolSupport.Key Buffer key,
                                    @RedisProtocolSupport.Tuple Collection<RedisProtocolSupport.BitfieldOperation> operations) throws Exception;

    /**
     * Perform bitwise operations between strings.
     *
     * @param operation the operation
     * @param destkey the destkey
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BITOP)
    public abstract String bitop(Buffer operation, @RedisProtocolSupport.Key Buffer destkey,
                                 @RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Perform bitwise operations between strings.
     *
     * @param operation the operation
     * @param destkey the destkey
     * @param key1 the key1
     * @param key2 the key2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BITOP)
    public abstract String bitop(Buffer operation, @RedisProtocolSupport.Key Buffer destkey,
                                 @RedisProtocolSupport.Key Buffer key1,
                                 @RedisProtocolSupport.Key Buffer key2) throws Exception;

    /**
     * Perform bitwise operations between strings.
     *
     * @param operation the operation
     * @param destkey the destkey
     * @param key1 the key1
     * @param key2 the key2
     * @param key3 the key3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BITOP)
    public abstract String bitop(Buffer operation, @RedisProtocolSupport.Key Buffer destkey,
                                 @RedisProtocolSupport.Key Buffer key1, @RedisProtocolSupport.Key Buffer key2,
                                 @RedisProtocolSupport.Key Buffer key3) throws Exception;

    /**
     * Perform bitwise operations between strings.
     *
     * @param operation the operation
     * @param destkey the destkey
     * @param keys the keys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BITOP)
    public abstract String bitop(Buffer operation, @RedisProtocolSupport.Key Buffer destkey,
                                 @RedisProtocolSupport.Key Collection<Buffer> keys) throws Exception;

    /**
     * Find first bit set or clear in a string.
     *
     * @param key the key
     * @param bit the bit
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BITPOS)
    public abstract String bitpos(@RedisProtocolSupport.Key Buffer key, long bit) throws Exception;

    /**
     * Find first bit set or clear in a string.
     *
     * @param key the key
     * @param bit the bit
     * @param start the start
     * @param end the end
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BITPOS)
    public abstract String bitpos(@RedisProtocolSupport.Key Buffer key, long bit, @Nullable Long start,
                                  @Nullable Long end) throws Exception;

    /**
     * Remove and get the first element in a list, or block until one is available.
     *
     * @param keys the keys
     * @param timeout the timeout
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BLPOP)
    public abstract String blpop(@RedisProtocolSupport.Key Collection<Buffer> keys, long timeout) throws Exception;

    /**
     * Remove and get the last element in a list, or block until one is available.
     *
     * @param keys the keys
     * @param timeout the timeout
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BRPOP)
    public abstract String brpop(@RedisProtocolSupport.Key Collection<Buffer> keys, long timeout) throws Exception;

    /**
     * Pop a value from a list, push it to another list and return it; or block until one is available.
     *
     * @param source the source
     * @param destination the destination
     * @param timeout the timeout
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BRPOPLPUSH)
    public abstract String brpoplpush(@RedisProtocolSupport.Key Buffer source,
                                      @RedisProtocolSupport.Key Buffer destination, long timeout) throws Exception;

    /**
     * Remove and return the member with the highest score from one or more sorted sets, or block until one is
     * available.
     *
     * @param keys the keys
     * @param timeout the timeout
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BZPOPMAX)
    public abstract String bzpopmax(@RedisProtocolSupport.Key Collection<Buffer> keys, long timeout) throws Exception;

    /**
     * Remove and return the member with the lowest score from one or more sorted sets, or block until one is available.
     *
     * @param keys the keys
     * @param timeout the timeout
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.BZPOPMIN)
    public abstract String bzpopmin(@RedisProtocolSupport.Key Collection<Buffer> keys, long timeout) throws Exception;

    /**
     * Kill the connection of a client.
     *
     * @param id the id
     * @param type the type
     * @param addrIpPort the addrIpPort
     * @param skipmeYesNo the skipmeYesNo
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLIENT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.KILL)
    public abstract String clientKill(@RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.ID) @Nullable Long id,
                                      @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ClientKillType type,
                                      @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.ADDR) @Nullable Buffer addrIpPort,
                                      @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.SKIPME) @Nullable Buffer skipmeYesNo) throws Exception;

    /**
     * Get the list of client connections.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLIENT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.LIST)
    public abstract String clientList() throws Exception;

    /**
     * Get the current connection name.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLIENT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.GETNAME)
    public abstract String clientGetname() throws Exception;

    /**
     * Stop processing commands from clients for some time.
     *
     * @param timeout the timeout
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLIENT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.PAUSE)
    public abstract String clientPause(long timeout) throws Exception;

    /**
     * Instruct the server whether to reply to commands.
     *
     * @param replyMode the replyMode
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLIENT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.REPLY)
    public abstract String clientReply(@RedisProtocolSupport.Option RedisProtocolSupport.ClientReplyReplyMode replyMode) throws Exception;

    /**
     * Set the current connection name.
     *
     * @param connectionName the connectionName
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLIENT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.SETNAME)
    public abstract String clientSetname(Buffer connectionName) throws Exception;

    /**
     * Assign new hash slots to receiving node.
     *
     * @param slot the slot
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.ADDSLOTS)
    public abstract String clusterAddslots(long slot) throws Exception;

    /**
     * Assign new hash slots to receiving node.
     *
     * @param slot1 the slot1
     * @param slot2 the slot2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.ADDSLOTS)
    public abstract String clusterAddslots(long slot1, long slot2) throws Exception;

    /**
     * Assign new hash slots to receiving node.
     *
     * @param slot1 the slot1
     * @param slot2 the slot2
     * @param slot3 the slot3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.ADDSLOTS)
    public abstract String clusterAddslots(long slot1, long slot2, long slot3) throws Exception;

    /**
     * Assign new hash slots to receiving node.
     *
     * @param slots the slots
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.ADDSLOTS)
    public abstract String clusterAddslots(Collection<Long> slots) throws Exception;

    /**
     * Return the number of failure reports active for a given node.
     *
     * @param nodeId the nodeId
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.COUNT_FAILURE_REPORTS)
    public abstract String clusterCountFailureReports(Buffer nodeId) throws Exception;

    /**
     * Return the number of local keys in the specified hash slot.
     *
     * @param slot the slot
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.COUNTKEYSINSLOT)
    public abstract String clusterCountkeysinslot(long slot) throws Exception;

    /**
     * Set hash slots as unbound in receiving node.
     *
     * @param slot the slot
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.DELSLOTS)
    public abstract String clusterDelslots(long slot) throws Exception;

    /**
     * Set hash slots as unbound in receiving node.
     *
     * @param slot1 the slot1
     * @param slot2 the slot2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.DELSLOTS)
    public abstract String clusterDelslots(long slot1, long slot2) throws Exception;

    /**
     * Set hash slots as unbound in receiving node.
     *
     * @param slot1 the slot1
     * @param slot2 the slot2
     * @param slot3 the slot3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.DELSLOTS)
    public abstract String clusterDelslots(long slot1, long slot2, long slot3) throws Exception;

    /**
     * Set hash slots as unbound in receiving node.
     *
     * @param slots the slots
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.DELSLOTS)
    public abstract String clusterDelslots(Collection<Long> slots) throws Exception;

    /**
     * Forces a slave to perform a manual failover of its master.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.FAILOVER)
    public abstract String clusterFailover() throws Exception;

    /**
     * Forces a slave to perform a manual failover of its master.
     *
     * @param options the options
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.FAILOVER)
    public abstract String clusterFailover(@RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ClusterFailoverOptions options) throws Exception;

    /**
     * Remove a node from the nodes table.
     *
     * @param nodeId the nodeId
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.FORGET)
    public abstract String clusterForget(Buffer nodeId) throws Exception;

    /**
     * Return local key names in the specified hash slot.
     *
     * @param slot the slot
     * @param count the count
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.GETKEYSINSLOT)
    public abstract String clusterGetkeysinslot(long slot, long count) throws Exception;

    /**
     * Provides info about Redis Cluster node state.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.INFO)
    public abstract String clusterInfo() throws Exception;

    /**
     * Returns the hash slot of the specified key.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.KEYSLOT)
    public abstract String clusterKeyslot(Buffer key) throws Exception;

    /**
     * Force a node cluster to handshake with another node.
     *
     * @param ip the ip
     * @param port the port
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.MEET)
    public abstract String clusterMeet(Buffer ip, long port) throws Exception;

    /**
     * Get Cluster config for the node.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.NODES)
    public abstract String clusterNodes() throws Exception;

    /**
     * Reconfigure a node as a slave of the specified master node.
     *
     * @param nodeId the nodeId
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.REPLICATE)
    public abstract String clusterReplicate(Buffer nodeId) throws Exception;

    /**
     * Reset a Redis Cluster node.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.RESET)
    public abstract String clusterReset() throws Exception;

    /**
     * Reset a Redis Cluster node.
     *
     * @param resetType the resetType
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.RESET)
    public abstract String clusterReset(@RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ClusterResetResetType resetType) throws Exception;

    /**
     * Forces the node to save cluster state on disk.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.SAVECONFIG)
    public abstract String clusterSaveconfig() throws Exception;

    /**
     * Set the configuration epoch in a new node.
     *
     * @param configEpoch the configEpoch
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.SET_CONFIG_EPOCH)
    public abstract String clusterSetConfigEpoch(long configEpoch) throws Exception;

    /**
     * Bind a hash slot to a specific node.
     *
     * @param slot the slot
     * @param subcommand the subcommand
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.SETSLOT)
    public abstract String clusterSetslot(long slot,
                                          @RedisProtocolSupport.Option RedisProtocolSupport.ClusterSetslotSubcommand subcommand) throws Exception;

    /**
     * Bind a hash slot to a specific node.
     *
     * @param slot the slot
     * @param subcommand the subcommand
     * @param nodeId the nodeId
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.SETSLOT)
    public abstract String clusterSetslot(long slot,
                                          @RedisProtocolSupport.Option RedisProtocolSupport.ClusterSetslotSubcommand subcommand,
                                          @Nullable Buffer nodeId) throws Exception;

    /**
     * List slave nodes of the specified master node.
     *
     * @param nodeId the nodeId
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.SLAVES)
    public abstract String clusterSlaves(Buffer nodeId) throws Exception;

    /**
     * Get array of Cluster slot to node mappings.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CLUSTER)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.SLOTS)
    public abstract String clusterSlots() throws Exception;

    /**
     * Get array of Redis command details.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.COMMAND)
    public abstract String command() throws Exception;

    /**
     * Get total number of Redis commands.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.COMMAND)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.COUNT)
    public abstract String commandCount() throws Exception;

    /**
     * Extract keys given a full Redis command.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.COMMAND)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.GETKEYS)
    public abstract String commandGetkeys() throws Exception;

    /**
     * Get array of specific Redis command details.
     *
     * @param commandName the commandName
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.COMMAND)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.INFO)
    public abstract String commandInfo(Buffer commandName) throws Exception;

    /**
     * Get array of specific Redis command details.
     *
     * @param commandName1 the commandName1
     * @param commandName2 the commandName2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.COMMAND)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.INFO)
    public abstract String commandInfo(Buffer commandName1, Buffer commandName2) throws Exception;

    /**
     * Get array of specific Redis command details.
     *
     * @param commandName1 the commandName1
     * @param commandName2 the commandName2
     * @param commandName3 the commandName3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.COMMAND)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.INFO)
    public abstract String commandInfo(Buffer commandName1, Buffer commandName2, Buffer commandName3) throws Exception;

    /**
     * Get array of specific Redis command details.
     *
     * @param commandNames the commandNames
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.COMMAND)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.INFO)
    public abstract String commandInfo(Collection<Buffer> commandNames) throws Exception;

    /**
     * Get the value of a configuration parameter.
     *
     * @param parameter the parameter
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CONFIG)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.GET)
    public abstract String configGet(Buffer parameter) throws Exception;

    /**
     * Rewrite the configuration file with the in memory configuration.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CONFIG)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.REWRITE)
    public abstract String configRewrite() throws Exception;

    /**
     * Set a configuration parameter to the given value.
     *
     * @param parameter the parameter
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CONFIG)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.SET)
    public abstract String configSet(Buffer parameter, Buffer value) throws Exception;

    /**
     * Reset the stats returned by INFO.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.CONFIG)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.RESETSTAT)
    public abstract String configResetstat() throws Exception;

    /**
     * Return the number of keys in the selected database.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.DBSIZE)
    public abstract String dbsize() throws Exception;

    /**
     * Get debugging information about a key.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.DEBUG)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.OBJECT)
    public abstract String debugObject(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Make the server crash.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.DEBUG)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.SEGFAULT)
    public abstract String debugSegfault() throws Exception;

    /**
     * Decrement the integer value of a key by one.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.DECR)
    public abstract String decr(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Decrement the integer value of a key by the given number.
     *
     * @param key the key
     * @param decrement the decrement
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.DECRBY)
    public abstract String decrby(@RedisProtocolSupport.Key Buffer key, long decrement) throws Exception;

    /**
     * Delete a key.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.DEL)
    public abstract String del(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Delete a key.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.DEL)
    public abstract String del(@RedisProtocolSupport.Key Buffer key1,
                               @RedisProtocolSupport.Key Buffer key2) throws Exception;

    /**
     * Delete a key.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @param key3 the key3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.DEL)
    public abstract String del(@RedisProtocolSupport.Key Buffer key1, @RedisProtocolSupport.Key Buffer key2,
                               @RedisProtocolSupport.Key Buffer key3) throws Exception;

    /**
     * Delete a key.
     *
     * @param keys the keys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.DEL)
    public abstract String del(@RedisProtocolSupport.Key Collection<Buffer> keys) throws Exception;

    /**
     * Discard all commands issued after MULTI.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.DISCARD)
    public abstract String discard() throws Exception;

    /**
     * Return a serialized version of the value stored at the specified key.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.DUMP)
    public abstract String dump(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Echo the given string.
     *
     * @param message the message
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ECHO)
    public abstract String echo(Buffer message) throws Exception;

    /**
     * Execute a Lua script server side.
     *
     * @param script the script
     * @param numkeys the numkeys
     * @param keys the keys
     * @param args the args
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.EVAL)
    public abstract String eval(Buffer script, long numkeys, @RedisProtocolSupport.Key Collection<Buffer> keys,
                                Collection<Buffer> args) throws Exception;

    /**
     * Execute a Lua script server side.
     *
     * @param script the script
     * @param numkeys the numkeys
     * @param keys the keys
     * @param args the args
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.EVAL)
    public abstract String evalList(Buffer script, long numkeys, @RedisProtocolSupport.Key Collection<Buffer> keys,
                                    Collection<Buffer> args) throws Exception;

    /**
     * Execute a Lua script server side.
     *
     * @param script the script
     * @param numkeys the numkeys
     * @param keys the keys
     * @param args the args
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.EVAL)
    public abstract String evalLong(Buffer script, long numkeys, @RedisProtocolSupport.Key Collection<Buffer> keys,
                                    Collection<Buffer> args) throws Exception;

    /**
     * Execute a Lua script server side.
     *
     * @param sha1 the sha1
     * @param numkeys the numkeys
     * @param keys the keys
     * @param args the args
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.EVALSHA)
    public abstract String evalsha(Buffer sha1, long numkeys, @RedisProtocolSupport.Key Collection<Buffer> keys,
                                   Collection<Buffer> args) throws Exception;

    /**
     * Execute a Lua script server side.
     *
     * @param sha1 the sha1
     * @param numkeys the numkeys
     * @param keys the keys
     * @param args the args
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.EVALSHA)
    public abstract String evalshaList(Buffer sha1, long numkeys, @RedisProtocolSupport.Key Collection<Buffer> keys,
                                       Collection<Buffer> args) throws Exception;

    /**
     * Execute a Lua script server side.
     *
     * @param sha1 the sha1
     * @param numkeys the numkeys
     * @param keys the keys
     * @param args the args
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.EVALSHA)
    public abstract String evalshaLong(Buffer sha1, long numkeys, @RedisProtocolSupport.Key Collection<Buffer> keys,
                                       Collection<Buffer> args) throws Exception;

    /**
     * Execute all commands issued after MULTI.
     *
     * @return a {@link List} result
     * @param <T> the type of elements
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.EXEC)
    public abstract <T> List<T> exec() throws Exception;

    /**
     * Determine if a key exists.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.EXISTS)
    public abstract String exists(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Determine if a key exists.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.EXISTS)
    public abstract String exists(@RedisProtocolSupport.Key Buffer key1,
                                  @RedisProtocolSupport.Key Buffer key2) throws Exception;

    /**
     * Determine if a key exists.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @param key3 the key3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.EXISTS)
    public abstract String exists(@RedisProtocolSupport.Key Buffer key1, @RedisProtocolSupport.Key Buffer key2,
                                  @RedisProtocolSupport.Key Buffer key3) throws Exception;

    /**
     * Determine if a key exists.
     *
     * @param keys the keys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.EXISTS)
    public abstract String exists(@RedisProtocolSupport.Key Collection<Buffer> keys) throws Exception;

    /**
     * Set a key's time to live in seconds.
     *
     * @param key the key
     * @param seconds the seconds
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.EXPIRE)
    public abstract String expire(@RedisProtocolSupport.Key Buffer key, long seconds) throws Exception;

    /**
     * Set the expiration for a key as a UNIX timestamp.
     *
     * @param key the key
     * @param timestamp the timestamp
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.EXPIREAT)
    public abstract String expireat(@RedisProtocolSupport.Key Buffer key, long timestamp) throws Exception;

    /**
     * Remove all keys from all databases.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.FLUSHALL)
    public abstract String flushall() throws Exception;

    /**
     * Remove all keys from all databases.
     *
     * @param async the async
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.FLUSHALL)
    public abstract String flushall(@RedisProtocolSupport.Option @Nullable RedisProtocolSupport.FlushallAsync async) throws Exception;

    /**
     * Remove all keys from the current database.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.FLUSHDB)
    public abstract String flushdb() throws Exception;

    /**
     * Remove all keys from the current database.
     *
     * @param async the async
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.FLUSHDB)
    public abstract String flushdb(@RedisProtocolSupport.Option @Nullable RedisProtocolSupport.FlushdbAsync async) throws Exception;

    /**
     * Add one or more geospatial items in the geospatial index represented using a sorted set.
     *
     * @param key the key
     * @param longitude the longitude
     * @param latitude the latitude
     * @param member the member
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEOADD)
    public abstract String geoadd(@RedisProtocolSupport.Key Buffer key, double longitude, double latitude,
                                  Buffer member) throws Exception;

    /**
     * Add one or more geospatial items in the geospatial index represented using a sorted set.
     *
     * @param key the key
     * @param longitude1 the longitude1
     * @param latitude1 the latitude1
     * @param member1 the member1
     * @param longitude2 the longitude2
     * @param latitude2 the latitude2
     * @param member2 the member2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEOADD)
    public abstract String geoadd(@RedisProtocolSupport.Key Buffer key, double longitude1, double latitude1,
                                  Buffer member1, double longitude2, double latitude2, Buffer member2) throws Exception;

    /**
     * Add one or more geospatial items in the geospatial index represented using a sorted set.
     *
     * @param key the key
     * @param longitude1 the longitude1
     * @param latitude1 the latitude1
     * @param member1 the member1
     * @param longitude2 the longitude2
     * @param latitude2 the latitude2
     * @param member2 the member2
     * @param longitude3 the longitude3
     * @param latitude3 the latitude3
     * @param member3 the member3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEOADD)
    public abstract String geoadd(@RedisProtocolSupport.Key Buffer key, double longitude1, double latitude1,
                                  Buffer member1, double longitude2, double latitude2, Buffer member2,
                                  double longitude3, double latitude3, Buffer member3) throws Exception;

    /**
     * Add one or more geospatial items in the geospatial index represented using a sorted set.
     *
     * @param key the key
     * @param longitudeLatitudeMembers the longitudeLatitudeMembers
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEOADD)
    public abstract String geoadd(@RedisProtocolSupport.Key Buffer key,
                                  @RedisProtocolSupport.Tuple Collection<RedisProtocolSupport.BufferLongitudeLatitudeMember> longitudeLatitudeMembers) throws Exception;

    /**
     * Returns the distance between two members of a geospatial index.
     *
     * @param key the key
     * @param member1 the member1
     * @param member2 the member2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEODIST)
    public abstract String geodist(@RedisProtocolSupport.Key Buffer key, Buffer member1,
                                   Buffer member2) throws Exception;

    /**
     * Returns the distance between two members of a geospatial index.
     *
     * @param key the key
     * @param member1 the member1
     * @param member2 the member2
     * @param unit the unit
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEODIST)
    public abstract String geodist(@RedisProtocolSupport.Key Buffer key, Buffer member1, Buffer member2,
                                   @Nullable Buffer unit) throws Exception;

    /**
     * Returns members of a geospatial index as standard geohash strings.
     *
     * @param key the key
     * @param member the member
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEOHASH)
    public abstract String geohash(@RedisProtocolSupport.Key Buffer key, Buffer member) throws Exception;

    /**
     * Returns members of a geospatial index as standard geohash strings.
     *
     * @param key the key
     * @param member1 the member1
     * @param member2 the member2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEOHASH)
    public abstract String geohash(@RedisProtocolSupport.Key Buffer key, Buffer member1,
                                   Buffer member2) throws Exception;

    /**
     * Returns members of a geospatial index as standard geohash strings.
     *
     * @param key the key
     * @param member1 the member1
     * @param member2 the member2
     * @param member3 the member3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEOHASH)
    public abstract String geohash(@RedisProtocolSupport.Key Buffer key, Buffer member1, Buffer member2,
                                   Buffer member3) throws Exception;

    /**
     * Returns members of a geospatial index as standard geohash strings.
     *
     * @param key the key
     * @param members the members
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEOHASH)
    public abstract String geohash(@RedisProtocolSupport.Key Buffer key, Collection<Buffer> members) throws Exception;

    /**
     * Returns longitude and latitude of members of a geospatial index.
     *
     * @param key the key
     * @param member the member
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEOPOS)
    public abstract String geopos(@RedisProtocolSupport.Key Buffer key, Buffer member) throws Exception;

    /**
     * Returns longitude and latitude of members of a geospatial index.
     *
     * @param key the key
     * @param member1 the member1
     * @param member2 the member2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEOPOS)
    public abstract String geopos(@RedisProtocolSupport.Key Buffer key, Buffer member1,
                                  Buffer member2) throws Exception;

    /**
     * Returns longitude and latitude of members of a geospatial index.
     *
     * @param key the key
     * @param member1 the member1
     * @param member2 the member2
     * @param member3 the member3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEOPOS)
    public abstract String geopos(@RedisProtocolSupport.Key Buffer key, Buffer member1, Buffer member2,
                                  Buffer member3) throws Exception;

    /**
     * Returns longitude and latitude of members of a geospatial index.
     *
     * @param key the key
     * @param members the members
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEOPOS)
    public abstract String geopos(@RedisProtocolSupport.Key Buffer key, Collection<Buffer> members) throws Exception;

    /**
     * Query a sorted set representing a geospatial index to fetch members matching a given maximum distance from a
     * point.
     *
     * @param key the key
     * @param longitude the longitude
     * @param latitude the latitude
     * @param radius the radius
     * @param unit the unit
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEORADIUS)
    public abstract String georadius(@RedisProtocolSupport.Key Buffer key, double longitude, double latitude,
                                     double radius,
                                     @RedisProtocolSupport.Option RedisProtocolSupport.GeoradiusUnit unit) throws Exception;

    /**
     * Query a sorted set representing a geospatial index to fetch members matching a given maximum distance from a
     * point.
     *
     * @param key the key
     * @param longitude the longitude
     * @param latitude the latitude
     * @param radius the radius
     * @param unit the unit
     * @param withcoord the withcoord
     * @param withdist the withdist
     * @param withhash the withhash
     * @param count the count
     * @param order the order
     * @param storeKey the storeKey
     * @param storedistKey the storedistKey
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEORADIUS)
    public abstract String georadius(@RedisProtocolSupport.Key Buffer key, double longitude, double latitude,
                                     double radius,
                                     @RedisProtocolSupport.Option RedisProtocolSupport.GeoradiusUnit unit,
                                     @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.GeoradiusWithcoord withcoord,
                                     @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.GeoradiusWithdist withdist,
                                     @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.GeoradiusWithhash withhash,
                                     @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.COUNT) @Nullable Long count,
                                     @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.GeoradiusOrder order,
                                     @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.STORE) @Nullable @RedisProtocolSupport.Key Buffer storeKey,
                                     @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.STOREDIST) @Nullable @RedisProtocolSupport.Key Buffer storedistKey) throws Exception;

    /**
     * Query a sorted set representing a geospatial index to fetch members matching a given maximum distance from a
     * member.
     *
     * @param key the key
     * @param member the member
     * @param radius the radius
     * @param unit the unit
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEORADIUSBYMEMBER)
    public abstract String georadiusbymember(@RedisProtocolSupport.Key Buffer key, Buffer member, double radius,
                                             @RedisProtocolSupport.Option RedisProtocolSupport.GeoradiusbymemberUnit unit) throws Exception;

    /**
     * Query a sorted set representing a geospatial index to fetch members matching a given maximum distance from a
     * member.
     *
     * @param key the key
     * @param member the member
     * @param radius the radius
     * @param unit the unit
     * @param withcoord the withcoord
     * @param withdist the withdist
     * @param withhash the withhash
     * @param count the count
     * @param order the order
     * @param storeKey the storeKey
     * @param storedistKey the storedistKey
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GEORADIUSBYMEMBER)
    public abstract String georadiusbymember(@RedisProtocolSupport.Key Buffer key, Buffer member, double radius,
                                             @RedisProtocolSupport.Option RedisProtocolSupport.GeoradiusbymemberUnit unit,
                                             @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.GeoradiusbymemberWithcoord withcoord,
                                             @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.GeoradiusbymemberWithdist withdist,
                                             @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.GeoradiusbymemberWithhash withhash,
                                             @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.COUNT) @Nullable Long count,
                                             @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.GeoradiusbymemberOrder order,
                                             @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.STORE) @Nullable @RedisProtocolSupport.Key Buffer storeKey,
                                             @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.STOREDIST) @Nullable @RedisProtocolSupport.Key Buffer storedistKey) throws Exception;

    /**
     * Get the value of a key.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GET)
    public abstract String get(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Returns the bit value at offset in the string value stored at key.
     *
     * @param key the key
     * @param offset the offset
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GETBIT)
    public abstract String getbit(@RedisProtocolSupport.Key Buffer key, long offset) throws Exception;

    /**
     * Get a substring of the string stored at a key.
     *
     * @param key the key
     * @param start the start
     * @param end the end
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GETRANGE)
    public abstract String getrange(@RedisProtocolSupport.Key Buffer key, long start, long end) throws Exception;

    /**
     * Set the string value of a key and return its old value.
     *
     * @param key the key
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.GETSET)
    public abstract String getset(@RedisProtocolSupport.Key Buffer key, Buffer value) throws Exception;

    /**
     * Delete one or more hash fields.
     *
     * @param key the key
     * @param field the field
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HDEL)
    public abstract String hdel(@RedisProtocolSupport.Key Buffer key, Buffer field) throws Exception;

    /**
     * Delete one or more hash fields.
     *
     * @param key the key
     * @param field1 the field1
     * @param field2 the field2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HDEL)
    public abstract String hdel(@RedisProtocolSupport.Key Buffer key, Buffer field1, Buffer field2) throws Exception;

    /**
     * Delete one or more hash fields.
     *
     * @param key the key
     * @param field1 the field1
     * @param field2 the field2
     * @param field3 the field3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HDEL)
    public abstract String hdel(@RedisProtocolSupport.Key Buffer key, Buffer field1, Buffer field2,
                                Buffer field3) throws Exception;

    /**
     * Delete one or more hash fields.
     *
     * @param key the key
     * @param fields the fields
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HDEL)
    public abstract String hdel(@RedisProtocolSupport.Key Buffer key, Collection<Buffer> fields) throws Exception;

    /**
     * Determine if a hash field exists.
     *
     * @param key the key
     * @param field the field
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HEXISTS)
    public abstract String hexists(@RedisProtocolSupport.Key Buffer key, Buffer field) throws Exception;

    /**
     * Get the value of a hash field.
     *
     * @param key the key
     * @param field the field
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HGET)
    public abstract String hget(@RedisProtocolSupport.Key Buffer key, Buffer field) throws Exception;

    /**
     * Get all the fields and values in a hash.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HGETALL)
    public abstract String hgetall(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Increment the integer value of a hash field by the given number.
     *
     * @param key the key
     * @param field the field
     * @param increment the increment
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HINCRBY)
    public abstract String hincrby(@RedisProtocolSupport.Key Buffer key, Buffer field, long increment) throws Exception;

    /**
     * Increment the float value of a hash field by the given amount.
     *
     * @param key the key
     * @param field the field
     * @param increment the increment
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HINCRBYFLOAT)
    public abstract String hincrbyfloat(@RedisProtocolSupport.Key Buffer key, Buffer field,
                                        double increment) throws Exception;

    /**
     * Get all the fields in a hash.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HKEYS)
    public abstract String hkeys(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Get the number of fields in a hash.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HLEN)
    public abstract String hlen(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Get the values of all the given hash fields.
     *
     * @param key the key
     * @param field the field
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HMGET)
    public abstract String hmget(@RedisProtocolSupport.Key Buffer key, Buffer field) throws Exception;

    /**
     * Get the values of all the given hash fields.
     *
     * @param key the key
     * @param field1 the field1
     * @param field2 the field2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HMGET)
    public abstract String hmget(@RedisProtocolSupport.Key Buffer key, Buffer field1, Buffer field2) throws Exception;

    /**
     * Get the values of all the given hash fields.
     *
     * @param key the key
     * @param field1 the field1
     * @param field2 the field2
     * @param field3 the field3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HMGET)
    public abstract String hmget(@RedisProtocolSupport.Key Buffer key, Buffer field1, Buffer field2,
                                 Buffer field3) throws Exception;

    /**
     * Get the values of all the given hash fields.
     *
     * @param key the key
     * @param fields the fields
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HMGET)
    public abstract String hmget(@RedisProtocolSupport.Key Buffer key, Collection<Buffer> fields) throws Exception;

    /**
     * Set multiple hash fields to multiple values.
     *
     * @param key the key
     * @param field the field
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HMSET)
    public abstract String hmset(@RedisProtocolSupport.Key Buffer key, Buffer field, Buffer value) throws Exception;

    /**
     * Set multiple hash fields to multiple values.
     *
     * @param key the key
     * @param field1 the field1
     * @param value1 the value1
     * @param field2 the field2
     * @param value2 the value2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HMSET)
    public abstract String hmset(@RedisProtocolSupport.Key Buffer key, Buffer field1, Buffer value1, Buffer field2,
                                 Buffer value2) throws Exception;

    /**
     * Set multiple hash fields to multiple values.
     *
     * @param key the key
     * @param field1 the field1
     * @param value1 the value1
     * @param field2 the field2
     * @param value2 the value2
     * @param field3 the field3
     * @param value3 the value3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HMSET)
    public abstract String hmset(@RedisProtocolSupport.Key Buffer key, Buffer field1, Buffer value1, Buffer field2,
                                 Buffer value2, Buffer field3, Buffer value3) throws Exception;

    /**
     * Set multiple hash fields to multiple values.
     *
     * @param key the key
     * @param fieldValues the fieldValues
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HMSET)
    public abstract String hmset(@RedisProtocolSupport.Key Buffer key,
                                 @RedisProtocolSupport.Tuple Collection<RedisProtocolSupport.BufferFieldValue> fieldValues) throws Exception;

    /**
     * Incrementally iterate hash fields and associated values.
     *
     * @param key the key
     * @param cursor the cursor
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HSCAN)
    public abstract String hscan(@RedisProtocolSupport.Key Buffer key, long cursor) throws Exception;

    /**
     * Incrementally iterate hash fields and associated values.
     *
     * @param key the key
     * @param cursor the cursor
     * @param matchPattern the matchPattern
     * @param count the count
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HSCAN)
    public abstract String hscan(@RedisProtocolSupport.Key Buffer key, long cursor,
                                 @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.MATCH) @Nullable Buffer matchPattern,
                                 @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.COUNT) @Nullable Long count) throws Exception;

    /**
     * Set the string value of a hash field.
     *
     * @param key the key
     * @param field the field
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HSET)
    public abstract String hset(@RedisProtocolSupport.Key Buffer key, Buffer field, Buffer value) throws Exception;

    /**
     * Set the value of a hash field, only if the field does not exist.
     *
     * @param key the key
     * @param field the field
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HSETNX)
    public abstract String hsetnx(@RedisProtocolSupport.Key Buffer key, Buffer field, Buffer value) throws Exception;

    /**
     * Get the length of the value of a hash field.
     *
     * @param key the key
     * @param field the field
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HSTRLEN)
    public abstract String hstrlen(@RedisProtocolSupport.Key Buffer key, Buffer field) throws Exception;

    /**
     * Get all the values in a hash.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.HVALS)
    public abstract String hvals(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Increment the integer value of a key by one.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.INCR)
    public abstract String incr(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Increment the integer value of a key by the given amount.
     *
     * @param key the key
     * @param increment the increment
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.INCRBY)
    public abstract String incrby(@RedisProtocolSupport.Key Buffer key, long increment) throws Exception;

    /**
     * Increment the float value of a key by the given amount.
     *
     * @param key the key
     * @param increment the increment
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.INCRBYFLOAT)
    public abstract String incrbyfloat(@RedisProtocolSupport.Key Buffer key, double increment) throws Exception;

    /**
     * Get information and statistics about the server.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.INFO)
    public abstract String info() throws Exception;

    /**
     * Get information and statistics about the server.
     *
     * @param section the section
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.INFO)
    public abstract String info(@Nullable Buffer section) throws Exception;

    /**
     * Find all keys matching the given pattern.
     *
     * @param pattern the pattern
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.KEYS)
    public abstract String keys(Buffer pattern) throws Exception;

    /**
     * Get the UNIX time stamp of the last successful save to disk.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.LASTSAVE)
    public abstract String lastsave() throws Exception;

    /**
     * Get an element from a list by its index.
     *
     * @param key the key
     * @param index the index
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.LINDEX)
    public abstract String lindex(@RedisProtocolSupport.Key Buffer key, long index) throws Exception;

    /**
     * Insert an element before or after another element in a list.
     *
     * @param key the key
     * @param where the where
     * @param pivot the pivot
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.LINSERT)
    public abstract String linsert(@RedisProtocolSupport.Key Buffer key,
                                   @RedisProtocolSupport.Option RedisProtocolSupport.LinsertWhere where, Buffer pivot,
                                   Buffer value) throws Exception;

    /**
     * Get the length of a list.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.LLEN)
    public abstract String llen(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Remove and get the first element in a list.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.LPOP)
    public abstract String lpop(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Prepend one or multiple values to a list.
     *
     * @param key the key
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.LPUSH)
    public abstract String lpush(@RedisProtocolSupport.Key Buffer key, Buffer value) throws Exception;

    /**
     * Prepend one or multiple values to a list.
     *
     * @param key the key
     * @param value1 the value1
     * @param value2 the value2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.LPUSH)
    public abstract String lpush(@RedisProtocolSupport.Key Buffer key, Buffer value1, Buffer value2) throws Exception;

    /**
     * Prepend one or multiple values to a list.
     *
     * @param key the key
     * @param value1 the value1
     * @param value2 the value2
     * @param value3 the value3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.LPUSH)
    public abstract String lpush(@RedisProtocolSupport.Key Buffer key, Buffer value1, Buffer value2,
                                 Buffer value3) throws Exception;

    /**
     * Prepend one or multiple values to a list.
     *
     * @param key the key
     * @param values the values
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.LPUSH)
    public abstract String lpush(@RedisProtocolSupport.Key Buffer key, Collection<Buffer> values) throws Exception;

    /**
     * Prepend a value to a list, only if the list exists.
     *
     * @param key the key
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.LPUSHX)
    public abstract String lpushx(@RedisProtocolSupport.Key Buffer key, Buffer value) throws Exception;

    /**
     * Get a range of elements from a list.
     *
     * @param key the key
     * @param start the start
     * @param stop the stop
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.LRANGE)
    public abstract String lrange(@RedisProtocolSupport.Key Buffer key, long start, long stop) throws Exception;

    /**
     * Remove elements from a list.
     *
     * @param key the key
     * @param count the count
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.LREM)
    public abstract String lrem(@RedisProtocolSupport.Key Buffer key, long count, Buffer value) throws Exception;

    /**
     * Set the value of an element in a list by its index.
     *
     * @param key the key
     * @param index the index
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.LSET)
    public abstract String lset(@RedisProtocolSupport.Key Buffer key, long index, Buffer value) throws Exception;

    /**
     * Trim a list to the specified range.
     *
     * @param key the key
     * @param start the start
     * @param stop the stop
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.LTRIM)
    public abstract String ltrim(@RedisProtocolSupport.Key Buffer key, long start, long stop) throws Exception;

    /**
     * Outputs memory problems report.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MEMORY)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.DOCTOR)
    public abstract String memoryDoctor() throws Exception;

    /**
     * Show helpful text about the different subcommands.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MEMORY)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.HELP)
    public abstract String memoryHelp() throws Exception;

    /**
     * Show allocator internal stats.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MEMORY)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.MALLOC_STATS)
    public abstract String memoryMallocStats() throws Exception;

    /**
     * Ask the allocator to release memory.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MEMORY)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.PURGE)
    public abstract String memoryPurge() throws Exception;

    /**
     * Show memory usage details.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MEMORY)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.STATS)
    public abstract String memoryStats() throws Exception;

    /**
     * Estimate the memory usage of a key.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MEMORY)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.USAGE)
    public abstract String memoryUsage(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Estimate the memory usage of a key.
     *
     * @param key the key
     * @param samplesCount the samplesCount
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MEMORY)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.USAGE)
    public abstract String memoryUsage(@RedisProtocolSupport.Key Buffer key,
                                       @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.SAMPLES) @Nullable Long samplesCount) throws Exception;

    /**
     * Get the values of all the given keys.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MGET)
    public abstract String mget(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Get the values of all the given keys.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MGET)
    public abstract String mget(@RedisProtocolSupport.Key Buffer key1,
                                @RedisProtocolSupport.Key Buffer key2) throws Exception;

    /**
     * Get the values of all the given keys.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @param key3 the key3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MGET)
    public abstract String mget(@RedisProtocolSupport.Key Buffer key1, @RedisProtocolSupport.Key Buffer key2,
                                @RedisProtocolSupport.Key Buffer key3) throws Exception;

    /**
     * Get the values of all the given keys.
     *
     * @param keys the keys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MGET)
    public abstract String mget(@RedisProtocolSupport.Key Collection<Buffer> keys) throws Exception;

    /**
     * Move a key to another database.
     *
     * @param key the key
     * @param db the db
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MOVE)
    public abstract String move(@RedisProtocolSupport.Key Buffer key, long db) throws Exception;

    /**
     * Set multiple keys to multiple values.
     *
     * @param key the key
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MSET)
    public abstract String mset(@RedisProtocolSupport.Key Buffer key, Buffer value) throws Exception;

    /**
     * Set multiple keys to multiple values.
     *
     * @param key1 the key1
     * @param value1 the value1
     * @param key2 the key2
     * @param value2 the value2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MSET)
    public abstract String mset(@RedisProtocolSupport.Key Buffer key1, Buffer value1,
                                @RedisProtocolSupport.Key Buffer key2, Buffer value2) throws Exception;

    /**
     * Set multiple keys to multiple values.
     *
     * @param key1 the key1
     * @param value1 the value1
     * @param key2 the key2
     * @param value2 the value2
     * @param key3 the key3
     * @param value3 the value3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MSET)
    public abstract String mset(@RedisProtocolSupport.Key Buffer key1, Buffer value1,
                                @RedisProtocolSupport.Key Buffer key2, Buffer value2,
                                @RedisProtocolSupport.Key Buffer key3, Buffer value3) throws Exception;

    /**
     * Set multiple keys to multiple values.
     *
     * @param keyValues the keyValues
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MSET)
    public abstract String mset(@RedisProtocolSupport.Tuple Collection<RedisProtocolSupport.BufferKeyValue> keyValues) throws Exception;

    /**
     * Set multiple keys to multiple values, only if none of the keys exist.
     *
     * @param key the key
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MSETNX)
    public abstract String msetnx(@RedisProtocolSupport.Key Buffer key, Buffer value) throws Exception;

    /**
     * Set multiple keys to multiple values, only if none of the keys exist.
     *
     * @param key1 the key1
     * @param value1 the value1
     * @param key2 the key2
     * @param value2 the value2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MSETNX)
    public abstract String msetnx(@RedisProtocolSupport.Key Buffer key1, Buffer value1,
                                  @RedisProtocolSupport.Key Buffer key2, Buffer value2) throws Exception;

    /**
     * Set multiple keys to multiple values, only if none of the keys exist.
     *
     * @param key1 the key1
     * @param value1 the value1
     * @param key2 the key2
     * @param value2 the value2
     * @param key3 the key3
     * @param value3 the value3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MSETNX)
    public abstract String msetnx(@RedisProtocolSupport.Key Buffer key1, Buffer value1,
                                  @RedisProtocolSupport.Key Buffer key2, Buffer value2,
                                  @RedisProtocolSupport.Key Buffer key3, Buffer value3) throws Exception;

    /**
     * Set multiple keys to multiple values, only if none of the keys exist.
     *
     * @param keyValues the keyValues
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.MSETNX)
    public abstract String msetnx(@RedisProtocolSupport.Tuple Collection<RedisProtocolSupport.BufferKeyValue> keyValues) throws Exception;

    /**
     * Returns the kind of internal representation used in order to store the value associated with a key.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.OBJECT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.ENCODING)
    public abstract String objectEncoding(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Returns the logarithmic access frequency counter of the object stored at the specified key.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.OBJECT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.FREQ)
    public abstract String objectFreq(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Returns a succinct help text.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.OBJECT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.HELP)
    public abstract String objectHelp() throws Exception;

    /**
     * Returns the number of seconds since the object stored at the specified key is idle.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.OBJECT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.IDLETIME)
    public abstract String objectIdletime(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Returns the number of references of the value associated with the specified key.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.OBJECT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.REFCOUNT)
    public abstract String objectRefcount(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Remove the expiration from a key.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PERSIST)
    public abstract String persist(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Set a key's time to live in milliseconds.
     *
     * @param key the key
     * @param milliseconds the milliseconds
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PEXPIRE)
    public abstract String pexpire(@RedisProtocolSupport.Key Buffer key, long milliseconds) throws Exception;

    /**
     * Set the expiration for a key as a UNIX timestamp specified in milliseconds.
     *
     * @param key the key
     * @param millisecondsTimestamp the millisecondsTimestamp
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PEXPIREAT)
    public abstract String pexpireat(@RedisProtocolSupport.Key Buffer key, long millisecondsTimestamp) throws Exception;

    /**
     * Adds the specified elements to the specified HyperLogLog.
     *
     * @param key the key
     * @param element the element
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PFADD)
    public abstract String pfadd(@RedisProtocolSupport.Key Buffer key, Buffer element) throws Exception;

    /**
     * Adds the specified elements to the specified HyperLogLog.
     *
     * @param key the key
     * @param element1 the element1
     * @param element2 the element2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PFADD)
    public abstract String pfadd(@RedisProtocolSupport.Key Buffer key, Buffer element1,
                                 Buffer element2) throws Exception;

    /**
     * Adds the specified elements to the specified HyperLogLog.
     *
     * @param key the key
     * @param element1 the element1
     * @param element2 the element2
     * @param element3 the element3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PFADD)
    public abstract String pfadd(@RedisProtocolSupport.Key Buffer key, Buffer element1, Buffer element2,
                                 Buffer element3) throws Exception;

    /**
     * Adds the specified elements to the specified HyperLogLog.
     *
     * @param key the key
     * @param elements the elements
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PFADD)
    public abstract String pfadd(@RedisProtocolSupport.Key Buffer key, Collection<Buffer> elements) throws Exception;

    /**
     * Return the approximated cardinality of the set(s) observed by the HyperLogLog at key(s).
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PFCOUNT)
    public abstract String pfcount(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Return the approximated cardinality of the set(s) observed by the HyperLogLog at key(s).
     *
     * @param key1 the key1
     * @param key2 the key2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PFCOUNT)
    public abstract String pfcount(@RedisProtocolSupport.Key Buffer key1,
                                   @RedisProtocolSupport.Key Buffer key2) throws Exception;

    /**
     * Return the approximated cardinality of the set(s) observed by the HyperLogLog at key(s).
     *
     * @param key1 the key1
     * @param key2 the key2
     * @param key3 the key3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PFCOUNT)
    public abstract String pfcount(@RedisProtocolSupport.Key Buffer key1, @RedisProtocolSupport.Key Buffer key2,
                                   @RedisProtocolSupport.Key Buffer key3) throws Exception;

    /**
     * Return the approximated cardinality of the set(s) observed by the HyperLogLog at key(s).
     *
     * @param keys the keys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PFCOUNT)
    public abstract String pfcount(@RedisProtocolSupport.Key Collection<Buffer> keys) throws Exception;

    /**
     * Merge N different HyperLogLogs into a single one.
     *
     * @param destkey the destkey
     * @param sourcekey the sourcekey
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PFMERGE)
    public abstract String pfmerge(@RedisProtocolSupport.Key Buffer destkey,
                                   @RedisProtocolSupport.Key Buffer sourcekey) throws Exception;

    /**
     * Merge N different HyperLogLogs into a single one.
     *
     * @param destkey the destkey
     * @param sourcekey1 the sourcekey1
     * @param sourcekey2 the sourcekey2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PFMERGE)
    public abstract String pfmerge(@RedisProtocolSupport.Key Buffer destkey,
                                   @RedisProtocolSupport.Key Buffer sourcekey1,
                                   @RedisProtocolSupport.Key Buffer sourcekey2) throws Exception;

    /**
     * Merge N different HyperLogLogs into a single one.
     *
     * @param destkey the destkey
     * @param sourcekey1 the sourcekey1
     * @param sourcekey2 the sourcekey2
     * @param sourcekey3 the sourcekey3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PFMERGE)
    public abstract String pfmerge(@RedisProtocolSupport.Key Buffer destkey,
                                   @RedisProtocolSupport.Key Buffer sourcekey1,
                                   @RedisProtocolSupport.Key Buffer sourcekey2,
                                   @RedisProtocolSupport.Key Buffer sourcekey3) throws Exception;

    /**
     * Merge N different HyperLogLogs into a single one.
     *
     * @param destkey the destkey
     * @param sourcekeys the sourcekeys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PFMERGE)
    public abstract String pfmerge(@RedisProtocolSupport.Key Buffer destkey,
                                   @RedisProtocolSupport.Key Collection<Buffer> sourcekeys) throws Exception;

    /**
     * Ping the server.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PING)
    public abstract String ping() throws Exception;

    /**
     * Ping the server.
     *
     * @param message the message
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PING)
    public abstract String ping(Buffer message) throws Exception;

    /**
     * Set the value and expiration in milliseconds of a key.
     *
     * @param key the key
     * @param milliseconds the milliseconds
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PSETEX)
    public abstract String psetex(@RedisProtocolSupport.Key Buffer key, long milliseconds,
                                  Buffer value) throws Exception;

    /**
     * Get the time to live for a key in milliseconds.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PTTL)
    public abstract String pttl(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Post a message to a channel.
     *
     * @param channel the channel
     * @param message the message
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PUBLISH)
    public abstract String publish(Buffer channel, Buffer message) throws Exception;

    /**
     * Lists the currently active channels.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PUBSUB)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.CHANNELS)
    public abstract String pubsubChannels() throws Exception;

    /**
     * Lists the currently active channels.
     *
     * @param pattern the pattern
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PUBSUB)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.CHANNELS)
    public abstract String pubsubChannels(@Nullable Buffer pattern) throws Exception;

    /**
     * Lists the currently active channels.
     *
     * @param pattern1 the pattern1
     * @param pattern2 the pattern2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PUBSUB)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.CHANNELS)
    public abstract String pubsubChannels(@Nullable Buffer pattern1, @Nullable Buffer pattern2) throws Exception;

    /**
     * Lists the currently active channels.
     *
     * @param pattern1 the pattern1
     * @param pattern2 the pattern2
     * @param pattern3 the pattern3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PUBSUB)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.CHANNELS)
    public abstract String pubsubChannels(@Nullable Buffer pattern1, @Nullable Buffer pattern2,
                                          @Nullable Buffer pattern3) throws Exception;

    /**
     * Lists the currently active channels.
     *
     * @param patterns the patterns
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PUBSUB)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.CHANNELS)
    public abstract String pubsubChannels(Collection<Buffer> patterns) throws Exception;

    /**
     * Returns the number of subscribers for the specified channels.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PUBSUB)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.NUMSUB)
    public abstract String pubsubNumsub() throws Exception;

    /**
     * Returns the number of subscribers for the specified channels.
     *
     * @param channel the channel
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PUBSUB)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.NUMSUB)
    public abstract String pubsubNumsub(@Nullable Buffer channel) throws Exception;

    /**
     * Returns the number of subscribers for the specified channels.
     *
     * @param channel1 the channel1
     * @param channel2 the channel2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PUBSUB)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.NUMSUB)
    public abstract String pubsubNumsub(@Nullable Buffer channel1, @Nullable Buffer channel2) throws Exception;

    /**
     * Returns the number of subscribers for the specified channels.
     *
     * @param channel1 the channel1
     * @param channel2 the channel2
     * @param channel3 the channel3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PUBSUB)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.NUMSUB)
    public abstract String pubsubNumsub(@Nullable Buffer channel1, @Nullable Buffer channel2,
                                        @Nullable Buffer channel3) throws Exception;

    /**
     * Returns the number of subscribers for the specified channels.
     *
     * @param channels the channels
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PUBSUB)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.NUMSUB)
    public abstract String pubsubNumsub(Collection<Buffer> channels) throws Exception;

    /**
     * Returns the number of subscriptions to patterns.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.PUBSUB)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.NUMPAT)
    public abstract String pubsubNumpat() throws Exception;

    /**
     * Return a random key from the keyspace.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.RANDOMKEY)
    public abstract String randomkey() throws Exception;

    /**
     * Enables read queries for a connection to a cluster slave node.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.READONLY)
    public abstract String readonly() throws Exception;

    /**
     * Disables read queries for a connection to a cluster slave node.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.READWRITE)
    public abstract String readwrite() throws Exception;

    /**
     * Rename a key.
     *
     * @param key the key
     * @param newkey the newkey
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.RENAME)
    public abstract String rename(@RedisProtocolSupport.Key Buffer key,
                                  @RedisProtocolSupport.Key Buffer newkey) throws Exception;

    /**
     * Rename a key, only if the new key does not exist.
     *
     * @param key the key
     * @param newkey the newkey
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.RENAMENX)
    public abstract String renamenx(@RedisProtocolSupport.Key Buffer key,
                                    @RedisProtocolSupport.Key Buffer newkey) throws Exception;

    /**
     * Create a key using the provided serialized value, previously obtained using DUMP.
     *
     * @param key the key
     * @param ttl the ttl
     * @param serializedValue the serializedValue
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.RESTORE)
    public abstract String restore(@RedisProtocolSupport.Key Buffer key, long ttl,
                                   Buffer serializedValue) throws Exception;

    /**
     * Create a key using the provided serialized value, previously obtained using DUMP.
     *
     * @param key the key
     * @param ttl the ttl
     * @param serializedValue the serializedValue
     * @param replace the replace
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.RESTORE)
    public abstract String restore(@RedisProtocolSupport.Key Buffer key, long ttl, Buffer serializedValue,
                                   @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.RestoreReplace replace) throws Exception;

    /**
     * Return the role of the instance in the context of replication.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ROLE)
    public abstract String role() throws Exception;

    /**
     * Remove and get the last element in a list.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.RPOP)
    public abstract String rpop(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Remove the last element in a list, prepend it to another list and return it.
     *
     * @param source the source
     * @param destination the destination
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.RPOPLPUSH)
    public abstract String rpoplpush(@RedisProtocolSupport.Key Buffer source,
                                     @RedisProtocolSupport.Key Buffer destination) throws Exception;

    /**
     * Append one or multiple values to a list.
     *
     * @param key the key
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.RPUSH)
    public abstract String rpush(@RedisProtocolSupport.Key Buffer key, Buffer value) throws Exception;

    /**
     * Append one or multiple values to a list.
     *
     * @param key the key
     * @param value1 the value1
     * @param value2 the value2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.RPUSH)
    public abstract String rpush(@RedisProtocolSupport.Key Buffer key, Buffer value1, Buffer value2) throws Exception;

    /**
     * Append one or multiple values to a list.
     *
     * @param key the key
     * @param value1 the value1
     * @param value2 the value2
     * @param value3 the value3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.RPUSH)
    public abstract String rpush(@RedisProtocolSupport.Key Buffer key, Buffer value1, Buffer value2,
                                 Buffer value3) throws Exception;

    /**
     * Append one or multiple values to a list.
     *
     * @param key the key
     * @param values the values
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.RPUSH)
    public abstract String rpush(@RedisProtocolSupport.Key Buffer key, Collection<Buffer> values) throws Exception;

    /**
     * Append a value to a list, only if the list exists.
     *
     * @param key the key
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.RPUSHX)
    public abstract String rpushx(@RedisProtocolSupport.Key Buffer key, Buffer value) throws Exception;

    /**
     * Add one or more members to a set.
     *
     * @param key the key
     * @param member the member
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SADD)
    public abstract String sadd(@RedisProtocolSupport.Key Buffer key, Buffer member) throws Exception;

    /**
     * Add one or more members to a set.
     *
     * @param key the key
     * @param member1 the member1
     * @param member2 the member2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SADD)
    public abstract String sadd(@RedisProtocolSupport.Key Buffer key, Buffer member1, Buffer member2) throws Exception;

    /**
     * Add one or more members to a set.
     *
     * @param key the key
     * @param member1 the member1
     * @param member2 the member2
     * @param member3 the member3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SADD)
    public abstract String sadd(@RedisProtocolSupport.Key Buffer key, Buffer member1, Buffer member2,
                                Buffer member3) throws Exception;

    /**
     * Add one or more members to a set.
     *
     * @param key the key
     * @param members the members
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SADD)
    public abstract String sadd(@RedisProtocolSupport.Key Buffer key, Collection<Buffer> members) throws Exception;

    /**
     * Synchronously save the dataset to disk.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SAVE)
    public abstract String save() throws Exception;

    /**
     * Incrementally iterate the keys space.
     *
     * @param cursor the cursor
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SCAN)
    public abstract String scan(long cursor) throws Exception;

    /**
     * Incrementally iterate the keys space.
     *
     * @param cursor the cursor
     * @param matchPattern the matchPattern
     * @param count the count
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SCAN)
    public abstract String scan(long cursor,
                                @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.MATCH) @Nullable Buffer matchPattern,
                                @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.COUNT) @Nullable Long count) throws Exception;

    /**
     * Get the number of members in a set.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SCARD)
    public abstract String scard(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Set the debug mode for executed scripts.
     *
     * @param mode the mode
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SCRIPT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.DEBUG)
    public abstract String scriptDebug(@RedisProtocolSupport.Option RedisProtocolSupport.ScriptDebugMode mode) throws Exception;

    /**
     * Check existence of scripts in the script cache.
     *
     * @param sha1 the sha1
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SCRIPT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.EXISTS)
    public abstract String scriptExists(Buffer sha1) throws Exception;

    /**
     * Check existence of scripts in the script cache.
     *
     * @param sha11 the sha11
     * @param sha12 the sha12
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SCRIPT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.EXISTS)
    public abstract String scriptExists(Buffer sha11, Buffer sha12) throws Exception;

    /**
     * Check existence of scripts in the script cache.
     *
     * @param sha11 the sha11
     * @param sha12 the sha12
     * @param sha13 the sha13
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SCRIPT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.EXISTS)
    public abstract String scriptExists(Buffer sha11, Buffer sha12, Buffer sha13) throws Exception;

    /**
     * Check existence of scripts in the script cache.
     *
     * @param sha1s the sha1s
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SCRIPT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.EXISTS)
    public abstract String scriptExists(Collection<Buffer> sha1s) throws Exception;

    /**
     * Remove all the scripts from the script cache.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SCRIPT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.FLUSH)
    public abstract String scriptFlush() throws Exception;

    /**
     * Kill the script currently in execution.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SCRIPT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.KILL)
    public abstract String scriptKill() throws Exception;

    /**
     * Load the specified Lua script into the script cache.
     *
     * @param script the script
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SCRIPT)
    @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.LOAD)
    public abstract String scriptLoad(Buffer script) throws Exception;

    /**
     * Subtract multiple sets.
     *
     * @param firstkey the firstkey
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SDIFF)
    public abstract String sdiff(@RedisProtocolSupport.Key Buffer firstkey) throws Exception;

    /**
     * Subtract multiple sets.
     *
     * @param firstkey the firstkey
     * @param otherkey the otherkey
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SDIFF)
    public abstract String sdiff(@RedisProtocolSupport.Key Buffer firstkey,
                                 @Nullable @RedisProtocolSupport.Key Buffer otherkey) throws Exception;

    /**
     * Subtract multiple sets.
     *
     * @param firstkey the firstkey
     * @param otherkey1 the otherkey1
     * @param otherkey2 the otherkey2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SDIFF)
    public abstract String sdiff(@RedisProtocolSupport.Key Buffer firstkey,
                                 @Nullable @RedisProtocolSupport.Key Buffer otherkey1,
                                 @Nullable @RedisProtocolSupport.Key Buffer otherkey2) throws Exception;

    /**
     * Subtract multiple sets.
     *
     * @param firstkey the firstkey
     * @param otherkey1 the otherkey1
     * @param otherkey2 the otherkey2
     * @param otherkey3 the otherkey3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SDIFF)
    public abstract String sdiff(@RedisProtocolSupport.Key Buffer firstkey,
                                 @Nullable @RedisProtocolSupport.Key Buffer otherkey1,
                                 @Nullable @RedisProtocolSupport.Key Buffer otherkey2,
                                 @Nullable @RedisProtocolSupport.Key Buffer otherkey3) throws Exception;

    /**
     * Subtract multiple sets.
     *
     * @param firstkey the firstkey
     * @param otherkeys the otherkeys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SDIFF)
    public abstract String sdiff(@RedisProtocolSupport.Key Buffer firstkey,
                                 @RedisProtocolSupport.Key Collection<Buffer> otherkeys) throws Exception;

    /**
     * Subtract multiple sets and store the resulting set in a key.
     *
     * @param destination the destination
     * @param firstkey the firstkey
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SDIFFSTORE)
    public abstract String sdiffstore(@RedisProtocolSupport.Key Buffer destination,
                                      @RedisProtocolSupport.Key Buffer firstkey) throws Exception;

    /**
     * Subtract multiple sets and store the resulting set in a key.
     *
     * @param destination the destination
     * @param firstkey the firstkey
     * @param otherkey the otherkey
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SDIFFSTORE)
    public abstract String sdiffstore(@RedisProtocolSupport.Key Buffer destination,
                                      @RedisProtocolSupport.Key Buffer firstkey,
                                      @Nullable @RedisProtocolSupport.Key Buffer otherkey) throws Exception;

    /**
     * Subtract multiple sets and store the resulting set in a key.
     *
     * @param destination the destination
     * @param firstkey the firstkey
     * @param otherkey1 the otherkey1
     * @param otherkey2 the otherkey2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SDIFFSTORE)
    public abstract String sdiffstore(@RedisProtocolSupport.Key Buffer destination,
                                      @RedisProtocolSupport.Key Buffer firstkey,
                                      @Nullable @RedisProtocolSupport.Key Buffer otherkey1,
                                      @Nullable @RedisProtocolSupport.Key Buffer otherkey2) throws Exception;

    /**
     * Subtract multiple sets and store the resulting set in a key.
     *
     * @param destination the destination
     * @param firstkey the firstkey
     * @param otherkey1 the otherkey1
     * @param otherkey2 the otherkey2
     * @param otherkey3 the otherkey3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SDIFFSTORE)
    public abstract String sdiffstore(@RedisProtocolSupport.Key Buffer destination,
                                      @RedisProtocolSupport.Key Buffer firstkey,
                                      @Nullable @RedisProtocolSupport.Key Buffer otherkey1,
                                      @Nullable @RedisProtocolSupport.Key Buffer otherkey2,
                                      @Nullable @RedisProtocolSupport.Key Buffer otherkey3) throws Exception;

    /**
     * Subtract multiple sets and store the resulting set in a key.
     *
     * @param destination the destination
     * @param firstkey the firstkey
     * @param otherkeys the otherkeys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SDIFFSTORE)
    public abstract String sdiffstore(@RedisProtocolSupport.Key Buffer destination,
                                      @RedisProtocolSupport.Key Buffer firstkey,
                                      @RedisProtocolSupport.Key Collection<Buffer> otherkeys) throws Exception;

    /**
     * Change the selected database for the current connection.
     *
     * @param index the index
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SELECT)
    public abstract String select(long index) throws Exception;

    /**
     * Set the string value of a key.
     *
     * @param key the key
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SET)
    public abstract String set(@RedisProtocolSupport.Key Buffer key, Buffer value) throws Exception;

    /**
     * Set the string value of a key.
     *
     * @param key the key
     * @param value the value
     * @param expireDuration the expireDuration
     * @param condition the condition
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SET)
    public abstract String set(@RedisProtocolSupport.Key Buffer key, Buffer value,
                               @RedisProtocolSupport.Tuple @Nullable RedisProtocolSupport.ExpireDuration expireDuration,
                               @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.SetCondition condition) throws Exception;

    /**
     * Sets or clears the bit at offset in the string value stored at key.
     *
     * @param key the key
     * @param offset the offset
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SETBIT)
    public abstract String setbit(@RedisProtocolSupport.Key Buffer key, long offset, Buffer value) throws Exception;

    /**
     * Set the value and expiration of a key.
     *
     * @param key the key
     * @param seconds the seconds
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SETEX)
    public abstract String setex(@RedisProtocolSupport.Key Buffer key, long seconds, Buffer value) throws Exception;

    /**
     * Set the value of a key, only if the key does not exist.
     *
     * @param key the key
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SETNX)
    public abstract String setnx(@RedisProtocolSupport.Key Buffer key, Buffer value) throws Exception;

    /**
     * Overwrite part of a string at key starting at the specified offset.
     *
     * @param key the key
     * @param offset the offset
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SETRANGE)
    public abstract String setrange(@RedisProtocolSupport.Key Buffer key, long offset, Buffer value) throws Exception;

    /**
     * Synchronously save the dataset to disk and then shut down the server.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SHUTDOWN)
    public abstract String shutdown() throws Exception;

    /**
     * Synchronously save the dataset to disk and then shut down the server.
     *
     * @param saveMode the saveMode
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SHUTDOWN)
    public abstract String shutdown(@RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ShutdownSaveMode saveMode) throws Exception;

    /**
     * Intersect multiple sets.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SINTER)
    public abstract String sinter(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Intersect multiple sets.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SINTER)
    public abstract String sinter(@RedisProtocolSupport.Key Buffer key1,
                                  @RedisProtocolSupport.Key Buffer key2) throws Exception;

    /**
     * Intersect multiple sets.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @param key3 the key3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SINTER)
    public abstract String sinter(@RedisProtocolSupport.Key Buffer key1, @RedisProtocolSupport.Key Buffer key2,
                                  @RedisProtocolSupport.Key Buffer key3) throws Exception;

    /**
     * Intersect multiple sets.
     *
     * @param keys the keys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SINTER)
    public abstract String sinter(@RedisProtocolSupport.Key Collection<Buffer> keys) throws Exception;

    /**
     * Intersect multiple sets and store the resulting set in a key.
     *
     * @param destination the destination
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SINTERSTORE)
    public abstract String sinterstore(@RedisProtocolSupport.Key Buffer destination,
                                       @RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Intersect multiple sets and store the resulting set in a key.
     *
     * @param destination the destination
     * @param key1 the key1
     * @param key2 the key2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SINTERSTORE)
    public abstract String sinterstore(@RedisProtocolSupport.Key Buffer destination,
                                       @RedisProtocolSupport.Key Buffer key1,
                                       @RedisProtocolSupport.Key Buffer key2) throws Exception;

    /**
     * Intersect multiple sets and store the resulting set in a key.
     *
     * @param destination the destination
     * @param key1 the key1
     * @param key2 the key2
     * @param key3 the key3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SINTERSTORE)
    public abstract String sinterstore(@RedisProtocolSupport.Key Buffer destination,
                                       @RedisProtocolSupport.Key Buffer key1, @RedisProtocolSupport.Key Buffer key2,
                                       @RedisProtocolSupport.Key Buffer key3) throws Exception;

    /**
     * Intersect multiple sets and store the resulting set in a key.
     *
     * @param destination the destination
     * @param keys the keys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SINTERSTORE)
    public abstract String sinterstore(@RedisProtocolSupport.Key Buffer destination,
                                       @RedisProtocolSupport.Key Collection<Buffer> keys) throws Exception;

    /**
     * Determine if a given value is a member of a set.
     *
     * @param key the key
     * @param member the member
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SISMEMBER)
    public abstract String sismember(@RedisProtocolSupport.Key Buffer key, Buffer member) throws Exception;

    /**
     * Make the server a slave of another instance, or promote it as master.
     *
     * @param host the host
     * @param port the port
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SLAVEOF)
    public abstract String slaveof(Buffer host, Buffer port) throws Exception;

    /**
     * Manages the Redis slow queries log.
     *
     * @param subcommand the subcommand
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SLOWLOG)
    public abstract String slowlog(Buffer subcommand) throws Exception;

    /**
     * Manages the Redis slow queries log.
     *
     * @param subcommand the subcommand
     * @param argument the argument
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SLOWLOG)
    public abstract String slowlog(Buffer subcommand, @Nullable Buffer argument) throws Exception;

    /**
     * Get all the members in a set.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SMEMBERS)
    public abstract String smembers(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Move a member from one set to another.
     *
     * @param source the source
     * @param destination the destination
     * @param member the member
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SMOVE)
    public abstract String smove(@RedisProtocolSupport.Key Buffer source, @RedisProtocolSupport.Key Buffer destination,
                                 Buffer member) throws Exception;

    /**
     * Sort the elements in a list, set or sorted set.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SORT)
    public abstract String sort(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Sort the elements in a list, set or sorted set.
     *
     * @param key the key
     * @param byPattern the byPattern
     * @param offsetCount the offsetCount
     * @param getPatterns the getPatterns
     * @param order the order
     * @param sorting the sorting
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SORT)
    public abstract String sort(@RedisProtocolSupport.Key Buffer key,
                                @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.BY) @Nullable Buffer byPattern,
                                @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.LIMIT) @Nullable @RedisProtocolSupport.Tuple RedisProtocolSupport.OffsetCount offsetCount,
                                @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.GET) Collection<Buffer> getPatterns,
                                @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.SortOrder order,
                                @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.SortSorting sorting) throws Exception;

    /**
     * Sort the elements in a list, set or sorted set.
     *
     * @param key the key
     * @param storeDestination the storeDestination
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SORT)
    public abstract String sort(@RedisProtocolSupport.Key Buffer key,
                                @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.STORE) @RedisProtocolSupport.Key Buffer storeDestination) throws Exception;

    /**
     * Sort the elements in a list, set or sorted set.
     *
     * @param key the key
     * @param storeDestination the storeDestination
     * @param byPattern the byPattern
     * @param offsetCount the offsetCount
     * @param getPatterns the getPatterns
     * @param order the order
     * @param sorting the sorting
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SORT)
    public abstract String sort(@RedisProtocolSupport.Key Buffer key,
                                @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.STORE) @RedisProtocolSupport.Key Buffer storeDestination,
                                @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.BY) @Nullable Buffer byPattern,
                                @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.LIMIT) @Nullable @RedisProtocolSupport.Tuple RedisProtocolSupport.OffsetCount offsetCount,
                                @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.GET) Collection<Buffer> getPatterns,
                                @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.SortOrder order,
                                @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.SortSorting sorting) throws Exception;

    /**
     * Remove and return one or multiple random members from a set.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SPOP)
    public abstract String spop(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Remove and return one or multiple random members from a set.
     *
     * @param key the key
     * @param count the count
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SPOP)
    public abstract String spop(@RedisProtocolSupport.Key Buffer key, @Nullable Long count) throws Exception;

    /**
     * Get one or multiple random members from a set.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SRANDMEMBER)
    public abstract String srandmember(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Get one or multiple random members from a set.
     *
     * @param key the key
     * @param count the count
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SRANDMEMBER)
    public abstract String srandmember(@RedisProtocolSupport.Key Buffer key, long count) throws Exception;

    /**
     * Remove one or more members from a set.
     *
     * @param key the key
     * @param member the member
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SREM)
    public abstract String srem(@RedisProtocolSupport.Key Buffer key, Buffer member) throws Exception;

    /**
     * Remove one or more members from a set.
     *
     * @param key the key
     * @param member1 the member1
     * @param member2 the member2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SREM)
    public abstract String srem(@RedisProtocolSupport.Key Buffer key, Buffer member1, Buffer member2) throws Exception;

    /**
     * Remove one or more members from a set.
     *
     * @param key the key
     * @param member1 the member1
     * @param member2 the member2
     * @param member3 the member3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SREM)
    public abstract String srem(@RedisProtocolSupport.Key Buffer key, Buffer member1, Buffer member2,
                                Buffer member3) throws Exception;

    /**
     * Remove one or more members from a set.
     *
     * @param key the key
     * @param members the members
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SREM)
    public abstract String srem(@RedisProtocolSupport.Key Buffer key, Collection<Buffer> members) throws Exception;

    /**
     * Incrementally iterate Set elements.
     *
     * @param key the key
     * @param cursor the cursor
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SSCAN)
    public abstract String sscan(@RedisProtocolSupport.Key Buffer key, long cursor) throws Exception;

    /**
     * Incrementally iterate Set elements.
     *
     * @param key the key
     * @param cursor the cursor
     * @param matchPattern the matchPattern
     * @param count the count
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SSCAN)
    public abstract String sscan(@RedisProtocolSupport.Key Buffer key, long cursor,
                                 @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.MATCH) @Nullable Buffer matchPattern,
                                 @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.COUNT) @Nullable Long count) throws Exception;

    /**
     * Get the length of the value stored in a key.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.STRLEN)
    public abstract String strlen(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Add multiple sets.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SUNION)
    public abstract String sunion(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Add multiple sets.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SUNION)
    public abstract String sunion(@RedisProtocolSupport.Key Buffer key1,
                                  @RedisProtocolSupport.Key Buffer key2) throws Exception;

    /**
     * Add multiple sets.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @param key3 the key3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SUNION)
    public abstract String sunion(@RedisProtocolSupport.Key Buffer key1, @RedisProtocolSupport.Key Buffer key2,
                                  @RedisProtocolSupport.Key Buffer key3) throws Exception;

    /**
     * Add multiple sets.
     *
     * @param keys the keys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SUNION)
    public abstract String sunion(@RedisProtocolSupport.Key Collection<Buffer> keys) throws Exception;

    /**
     * Add multiple sets and store the resulting set in a key.
     *
     * @param destination the destination
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SUNIONSTORE)
    public abstract String sunionstore(@RedisProtocolSupport.Key Buffer destination,
                                       @RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Add multiple sets and store the resulting set in a key.
     *
     * @param destination the destination
     * @param key1 the key1
     * @param key2 the key2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SUNIONSTORE)
    public abstract String sunionstore(@RedisProtocolSupport.Key Buffer destination,
                                       @RedisProtocolSupport.Key Buffer key1,
                                       @RedisProtocolSupport.Key Buffer key2) throws Exception;

    /**
     * Add multiple sets and store the resulting set in a key.
     *
     * @param destination the destination
     * @param key1 the key1
     * @param key2 the key2
     * @param key3 the key3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SUNIONSTORE)
    public abstract String sunionstore(@RedisProtocolSupport.Key Buffer destination,
                                       @RedisProtocolSupport.Key Buffer key1, @RedisProtocolSupport.Key Buffer key2,
                                       @RedisProtocolSupport.Key Buffer key3) throws Exception;

    /**
     * Add multiple sets and store the resulting set in a key.
     *
     * @param destination the destination
     * @param keys the keys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SUNIONSTORE)
    public abstract String sunionstore(@RedisProtocolSupport.Key Buffer destination,
                                       @RedisProtocolSupport.Key Collection<Buffer> keys) throws Exception;

    /**
     * Swaps two Redis databases.
     *
     * @param index the index
     * @param index1 the index1
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.SWAPDB)
    public abstract String swapdb(long index, long index1) throws Exception;

    /**
     * Return the current server time.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.TIME)
    public abstract String time() throws Exception;

    /**
     * Alters the last access time of a key(s). Returns the number of existing keys specified.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.TOUCH)
    public abstract String touch(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Alters the last access time of a key(s). Returns the number of existing keys specified.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.TOUCH)
    public abstract String touch(@RedisProtocolSupport.Key Buffer key1,
                                 @RedisProtocolSupport.Key Buffer key2) throws Exception;

    /**
     * Alters the last access time of a key(s). Returns the number of existing keys specified.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @param key3 the key3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.TOUCH)
    public abstract String touch(@RedisProtocolSupport.Key Buffer key1, @RedisProtocolSupport.Key Buffer key2,
                                 @RedisProtocolSupport.Key Buffer key3) throws Exception;

    /**
     * Alters the last access time of a key(s). Returns the number of existing keys specified.
     *
     * @param keys the keys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.TOUCH)
    public abstract String touch(@RedisProtocolSupport.Key Collection<Buffer> keys) throws Exception;

    /**
     * Get the time to live for a key.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.TTL)
    public abstract String ttl(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Determine the type stored at key.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.TYPE)
    public abstract String type(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Delete a key asynchronously in another thread. Otherwise it is just as DEL, but non blocking.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.UNLINK)
    public abstract String unlink(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Delete a key asynchronously in another thread. Otherwise it is just as DEL, but non blocking.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.UNLINK)
    public abstract String unlink(@RedisProtocolSupport.Key Buffer key1,
                                  @RedisProtocolSupport.Key Buffer key2) throws Exception;

    /**
     * Delete a key asynchronously in another thread. Otherwise it is just as DEL, but non blocking.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @param key3 the key3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.UNLINK)
    public abstract String unlink(@RedisProtocolSupport.Key Buffer key1, @RedisProtocolSupport.Key Buffer key2,
                                  @RedisProtocolSupport.Key Buffer key3) throws Exception;

    /**
     * Delete a key asynchronously in another thread. Otherwise it is just as DEL, but non blocking.
     *
     * @param keys the keys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.UNLINK)
    public abstract String unlink(@RedisProtocolSupport.Key Collection<Buffer> keys) throws Exception;

    /**
     * Forget about all watched keys.
     *
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.UNWATCH)
    public abstract String unwatch() throws Exception;

    /**
     * Wait for the synchronous replication of all the write commands sent in the context of the current connection.
     *
     * @param numslaves the numslaves
     * @param timeout the timeout
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.WAIT)
    public abstract String wait(long numslaves, long timeout) throws Exception;

    /**
     * Watch the given keys to determine execution of the MULTI/EXEC block.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.WATCH)
    public abstract String watch(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Watch the given keys to determine execution of the MULTI/EXEC block.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.WATCH)
    public abstract String watch(@RedisProtocolSupport.Key Buffer key1,
                                 @RedisProtocolSupport.Key Buffer key2) throws Exception;

    /**
     * Watch the given keys to determine execution of the MULTI/EXEC block.
     *
     * @param key1 the key1
     * @param key2 the key2
     * @param key3 the key3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.WATCH)
    public abstract String watch(@RedisProtocolSupport.Key Buffer key1, @RedisProtocolSupport.Key Buffer key2,
                                 @RedisProtocolSupport.Key Buffer key3) throws Exception;

    /**
     * Watch the given keys to determine execution of the MULTI/EXEC block.
     *
     * @param keys the keys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.WATCH)
    public abstract String watch(@RedisProtocolSupport.Key Collection<Buffer> keys) throws Exception;

    /**
     * Appends a new entry to a stream.
     *
     * @param key the key
     * @param id the id
     * @param field the field
     * @param value the value
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.XADD)
    public abstract String xadd(@RedisProtocolSupport.Key Buffer key, Buffer id, Buffer field,
                                Buffer value) throws Exception;

    /**
     * Appends a new entry to a stream.
     *
     * @param key the key
     * @param id the id
     * @param field1 the field1
     * @param value1 the value1
     * @param field2 the field2
     * @param value2 the value2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.XADD)
    public abstract String xadd(@RedisProtocolSupport.Key Buffer key, Buffer id, Buffer field1, Buffer value1,
                                Buffer field2, Buffer value2) throws Exception;

    /**
     * Appends a new entry to a stream.
     *
     * @param key the key
     * @param id the id
     * @param field1 the field1
     * @param value1 the value1
     * @param field2 the field2
     * @param value2 the value2
     * @param field3 the field3
     * @param value3 the value3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.XADD)
    public abstract String xadd(@RedisProtocolSupport.Key Buffer key, Buffer id, Buffer field1, Buffer value1,
                                Buffer field2, Buffer value2, Buffer field3, Buffer value3) throws Exception;

    /**
     * Appends a new entry to a stream.
     *
     * @param key the key
     * @param id the id
     * @param fieldValues the fieldValues
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.XADD)
    public abstract String xadd(@RedisProtocolSupport.Key Buffer key, Buffer id,
                                @RedisProtocolSupport.Tuple Collection<RedisProtocolSupport.BufferFieldValue> fieldValues) throws Exception;

    /**
     * Return the number of entires in a stream.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.XLEN)
    public abstract String xlen(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Return information and entries from a stream conusmer group pending entries list, that are messages fetched but
     * never acknowledged.
     *
     * @param key the key
     * @param group the group
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.XPENDING)
    public abstract String xpending(@RedisProtocolSupport.Key Buffer key, Buffer group) throws Exception;

    /**
     * Return information and entries from a stream conusmer group pending entries list, that are messages fetched but
     * never acknowledged.
     *
     * @param key the key
     * @param group the group
     * @param start the start
     * @param end the end
     * @param count the count
     * @param consumer the consumer
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.XPENDING)
    public abstract String xpending(@RedisProtocolSupport.Key Buffer key, Buffer group, @Nullable Buffer start,
                                    @Nullable Buffer end, @Nullable Long count,
                                    @Nullable Buffer consumer) throws Exception;

    /**
     * Return a range of elements in a stream, with IDs matching the specified IDs interval.
     *
     * @param key the key
     * @param start the start
     * @param end the end
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.XRANGE)
    public abstract String xrange(@RedisProtocolSupport.Key Buffer key, Buffer start, Buffer end) throws Exception;

    /**
     * Return a range of elements in a stream, with IDs matching the specified IDs interval.
     *
     * @param key the key
     * @param start the start
     * @param end the end
     * @param count the count
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.XRANGE)
    public abstract String xrange(@RedisProtocolSupport.Key Buffer key, Buffer start, Buffer end,
                                  @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.COUNT) @Nullable Long count) throws Exception;

    /**
     * Return never seen elements in multiple streams, with IDs greater than the ones reported by the caller for each
     * stream. Can block.
     *
     * @param keys the keys
     * @param ids the ids
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.XREAD)
    public abstract String xread(@RedisProtocolSupport.Key Collection<Buffer> keys,
                                 Collection<Buffer> ids) throws Exception;

    /**
     * Return never seen elements in multiple streams, with IDs greater than the ones reported by the caller for each
     * stream. Can block.
     *
     * @param count the count
     * @param blockMilliseconds the blockMilliseconds
     * @param keys the keys
     * @param ids the ids
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.XREAD)
    public abstract String xread(@RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.COUNT) @Nullable Long count,
                                 @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.BLOCK) @Nullable Long blockMilliseconds,
                                 @RedisProtocolSupport.Key Collection<Buffer> keys,
                                 Collection<Buffer> ids) throws Exception;

    /**
     * Return new entries from a stream using a consumer group, or access the history of the pending entries for a given
     * consumer. Can block.
     *
     * @param groupConsumer the groupConsumer
     * @param keys the keys
     * @param ids the ids
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.XREADGROUP)
    public abstract String xreadgroup(@RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.GROUP) @RedisProtocolSupport.Tuple RedisProtocolSupport.BufferGroupConsumer groupConsumer,
                                      @RedisProtocolSupport.Key Collection<Buffer> keys,
                                      Collection<Buffer> ids) throws Exception;

    /**
     * Return new entries from a stream using a consumer group, or access the history of the pending entries for a given
     * consumer. Can block.
     *
     * @param groupConsumer the groupConsumer
     * @param count the count
     * @param blockMilliseconds the blockMilliseconds
     * @param keys the keys
     * @param ids the ids
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.XREADGROUP)
    public abstract String xreadgroup(@RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.GROUP) @RedisProtocolSupport.Tuple RedisProtocolSupport.BufferGroupConsumer groupConsumer,
                                      @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.COUNT) @Nullable Long count,
                                      @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.BLOCK) @Nullable Long blockMilliseconds,
                                      @RedisProtocolSupport.Key Collection<Buffer> keys,
                                      Collection<Buffer> ids) throws Exception;

    /**
     * Return a range of elements in a stream, with IDs matching the specified IDs interval, in reverse order (from
     * greater to smaller IDs) compared to XRANGE.
     *
     * @param key the key
     * @param end the end
     * @param start the start
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.XREVRANGE)
    public abstract String xrevrange(@RedisProtocolSupport.Key Buffer key, Buffer end, Buffer start) throws Exception;

    /**
     * Return a range of elements in a stream, with IDs matching the specified IDs interval, in reverse order (from
     * greater to smaller IDs) compared to XRANGE.
     *
     * @param key the key
     * @param end the end
     * @param start the start
     * @param count the count
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.XREVRANGE)
    public abstract String xrevrange(@RedisProtocolSupport.Key Buffer key, Buffer end, Buffer start,
                                     @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.COUNT) @Nullable Long count) throws Exception;

    /**
     * Add one or more members to a sorted set, or update its score if it already exists.
     *
     * @param key the key
     * @param scoreMembers the scoreMembers
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZADD)
    public abstract String zadd(@RedisProtocolSupport.Key Buffer key,
                                @RedisProtocolSupport.Tuple Collection<RedisProtocolSupport.BufferScoreMember> scoreMembers) throws Exception;

    /**
     * Add one or more members to a sorted set, or update its score if it already exists.
     *
     * @param key the key
     * @param condition the condition
     * @param change the change
     * @param score the score
     * @param member the member
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZADD)
    public abstract String zadd(@RedisProtocolSupport.Key Buffer key,
                                @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddCondition condition,
                                @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddChange change,
                                double score, Buffer member) throws Exception;

    /**
     * Add one or more members to a sorted set, or update its score if it already exists.
     *
     * @param key the key
     * @param condition the condition
     * @param change the change
     * @param score1 the score1
     * @param member1 the member1
     * @param score2 the score2
     * @param member2 the member2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZADD)
    public abstract String zadd(@RedisProtocolSupport.Key Buffer key,
                                @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddCondition condition,
                                @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddChange change,
                                double score1, Buffer member1, double score2, Buffer member2) throws Exception;

    /**
     * Add one or more members to a sorted set, or update its score if it already exists.
     *
     * @param key the key
     * @param condition the condition
     * @param change the change
     * @param score1 the score1
     * @param member1 the member1
     * @param score2 the score2
     * @param member2 the member2
     * @param score3 the score3
     * @param member3 the member3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZADD)
    public abstract String zadd(@RedisProtocolSupport.Key Buffer key,
                                @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddCondition condition,
                                @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddChange change,
                                double score1, Buffer member1, double score2, Buffer member2, double score3,
                                Buffer member3) throws Exception;

    /**
     * Add one or more members to a sorted set, or update its score if it already exists.
     *
     * @param key the key
     * @param condition the condition
     * @param change the change
     * @param scoreMembers the scoreMembers
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZADD)
    public abstract String zadd(@RedisProtocolSupport.Key Buffer key,
                                @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddCondition condition,
                                @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddChange change,
                                @RedisProtocolSupport.Tuple Collection<RedisProtocolSupport.BufferScoreMember> scoreMembers) throws Exception;

    /**
     * Add one or more members to a sorted set, or update its score if it already exists.
     *
     * @param key the key
     * @param scoreMembers the scoreMembers
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZADD)
    public abstract String zaddIncr(@RedisProtocolSupport.Key Buffer key,
                                    @RedisProtocolSupport.Tuple Collection<RedisProtocolSupport.BufferScoreMember> scoreMembers) throws Exception;

    /**
     * Add one or more members to a sorted set, or update its score if it already exists.
     *
     * @param key the key
     * @param condition the condition
     * @param change the change
     * @param score the score
     * @param member the member
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZADD)
    public abstract String zaddIncr(@RedisProtocolSupport.Key Buffer key,
                                    @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddCondition condition,
                                    @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddChange change,
                                    double score, Buffer member) throws Exception;

    /**
     * Add one or more members to a sorted set, or update its score if it already exists.
     *
     * @param key the key
     * @param condition the condition
     * @param change the change
     * @param score1 the score1
     * @param member1 the member1
     * @param score2 the score2
     * @param member2 the member2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZADD)
    public abstract String zaddIncr(@RedisProtocolSupport.Key Buffer key,
                                    @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddCondition condition,
                                    @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddChange change,
                                    double score1, Buffer member1, double score2, Buffer member2) throws Exception;

    /**
     * Add one or more members to a sorted set, or update its score if it already exists.
     *
     * @param key the key
     * @param condition the condition
     * @param change the change
     * @param score1 the score1
     * @param member1 the member1
     * @param score2 the score2
     * @param member2 the member2
     * @param score3 the score3
     * @param member3 the member3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZADD)
    public abstract String zaddIncr(@RedisProtocolSupport.Key Buffer key,
                                    @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddCondition condition,
                                    @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddChange change,
                                    double score1, Buffer member1, double score2, Buffer member2, double score3,
                                    Buffer member3) throws Exception;

    /**
     * Add one or more members to a sorted set, or update its score if it already exists.
     *
     * @param key the key
     * @param condition the condition
     * @param change the change
     * @param scoreMembers the scoreMembers
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZADD)
    public abstract String zaddIncr(@RedisProtocolSupport.Key Buffer key,
                                    @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddCondition condition,
                                    @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZaddChange change,
                                    @RedisProtocolSupport.Tuple Collection<RedisProtocolSupport.BufferScoreMember> scoreMembers) throws Exception;

    /**
     * Get the number of members in a sorted set.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZCARD)
    public abstract String zcard(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Count the members in a sorted set with scores within the given values.
     *
     * @param key the key
     * @param min the min
     * @param max the max
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZCOUNT)
    public abstract String zcount(@RedisProtocolSupport.Key Buffer key, double min, double max) throws Exception;

    /**
     * Increment the score of a member in a sorted set.
     *
     * @param key the key
     * @param increment the increment
     * @param member the member
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZINCRBY)
    public abstract String zincrby(@RedisProtocolSupport.Key Buffer key, long increment,
                                   Buffer member) throws Exception;

    /**
     * Intersect multiple sorted sets and store the resulting sorted set in a new key.
     *
     * @param destination the destination
     * @param numkeys the numkeys
     * @param keys the keys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZINTERSTORE)
    public abstract String zinterstore(@RedisProtocolSupport.Key Buffer destination, long numkeys,
                                       @RedisProtocolSupport.Key Collection<Buffer> keys) throws Exception;

    /**
     * Intersect multiple sorted sets and store the resulting sorted set in a new key.
     *
     * @param destination the destination
     * @param numkeys the numkeys
     * @param keys the keys
     * @param weightses the weightses
     * @param aggregate the aggregate
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZINTERSTORE)
    public abstract String zinterstore(@RedisProtocolSupport.Key Buffer destination, long numkeys,
                                       @RedisProtocolSupport.Key Collection<Buffer> keys,
                                       @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.WEIGHTS) Collection<Long> weightses,
                                       @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZinterstoreAggregate aggregate) throws Exception;

    /**
     * Count the number of members in a sorted set between a given lexicographical range.
     *
     * @param key the key
     * @param min the min
     * @param max the max
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZLEXCOUNT)
    public abstract String zlexcount(@RedisProtocolSupport.Key Buffer key, Buffer min, Buffer max) throws Exception;

    /**
     * Remove and return members with the highest scores in a sorted set.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZPOPMAX)
    public abstract String zpopmax(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Remove and return members with the highest scores in a sorted set.
     *
     * @param key the key
     * @param count the count
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZPOPMAX)
    public abstract String zpopmax(@RedisProtocolSupport.Key Buffer key, @Nullable Long count) throws Exception;

    /**
     * Remove and return members with the lowest scores in a sorted set.
     *
     * @param key the key
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZPOPMIN)
    public abstract String zpopmin(@RedisProtocolSupport.Key Buffer key) throws Exception;

    /**
     * Remove and return members with the lowest scores in a sorted set.
     *
     * @param key the key
     * @param count the count
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZPOPMIN)
    public abstract String zpopmin(@RedisProtocolSupport.Key Buffer key, @Nullable Long count) throws Exception;

    /**
     * Return a range of members in a sorted set, by index.
     *
     * @param key the key
     * @param start the start
     * @param stop the stop
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZRANGE)
    public abstract String zrange(@RedisProtocolSupport.Key Buffer key, long start, long stop) throws Exception;

    /**
     * Return a range of members in a sorted set, by index.
     *
     * @param key the key
     * @param start the start
     * @param stop the stop
     * @param withscores the withscores
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZRANGE)
    public abstract String zrange(@RedisProtocolSupport.Key Buffer key, long start, long stop,
                                  @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZrangeWithscores withscores) throws Exception;

    /**
     * Return a range of members in a sorted set, by lexicographical range.
     *
     * @param key the key
     * @param min the min
     * @param max the max
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZRANGEBYLEX)
    public abstract String zrangebylex(@RedisProtocolSupport.Key Buffer key, Buffer min, Buffer max) throws Exception;

    /**
     * Return a range of members in a sorted set, by lexicographical range.
     *
     * @param key the key
     * @param min the min
     * @param max the max
     * @param offsetCount the offsetCount
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZRANGEBYLEX)
    public abstract String zrangebylex(@RedisProtocolSupport.Key Buffer key, Buffer min, Buffer max,
                                       @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.LIMIT) @Nullable @RedisProtocolSupport.Tuple RedisProtocolSupport.OffsetCount offsetCount) throws Exception;

    /**
     * Return a range of members in a sorted set, by score.
     *
     * @param key the key
     * @param min the min
     * @param max the max
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZRANGEBYSCORE)
    public abstract String zrangebyscore(@RedisProtocolSupport.Key Buffer key, double min, double max) throws Exception;

    /**
     * Return a range of members in a sorted set, by score.
     *
     * @param key the key
     * @param min the min
     * @param max the max
     * @param withscores the withscores
     * @param offsetCount the offsetCount
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZRANGEBYSCORE)
    public abstract String zrangebyscore(@RedisProtocolSupport.Key Buffer key, double min, double max,
                                         @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZrangebyscoreWithscores withscores,
                                         @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.LIMIT) @Nullable @RedisProtocolSupport.Tuple RedisProtocolSupport.OffsetCount offsetCount) throws Exception;

    /**
     * Determine the index of a member in a sorted set.
     *
     * @param key the key
     * @param member the member
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZRANK)
    public abstract String zrank(@RedisProtocolSupport.Key Buffer key, Buffer member) throws Exception;

    /**
     * Remove one or more members from a sorted set.
     *
     * @param key the key
     * @param member the member
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZREM)
    public abstract String zrem(@RedisProtocolSupport.Key Buffer key, Buffer member) throws Exception;

    /**
     * Remove one or more members from a sorted set.
     *
     * @param key the key
     * @param member1 the member1
     * @param member2 the member2
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZREM)
    public abstract String zrem(@RedisProtocolSupport.Key Buffer key, Buffer member1, Buffer member2) throws Exception;

    /**
     * Remove one or more members from a sorted set.
     *
     * @param key the key
     * @param member1 the member1
     * @param member2 the member2
     * @param member3 the member3
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZREM)
    public abstract String zrem(@RedisProtocolSupport.Key Buffer key, Buffer member1, Buffer member2,
                                Buffer member3) throws Exception;

    /**
     * Remove one or more members from a sorted set.
     *
     * @param key the key
     * @param members the members
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZREM)
    public abstract String zrem(@RedisProtocolSupport.Key Buffer key, Collection<Buffer> members) throws Exception;

    /**
     * Remove all members in a sorted set between the given lexicographical range.
     *
     * @param key the key
     * @param min the min
     * @param max the max
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZREMRANGEBYLEX)
    public abstract String zremrangebylex(@RedisProtocolSupport.Key Buffer key, Buffer min,
                                          Buffer max) throws Exception;

    /**
     * Remove all members in a sorted set within the given indexes.
     *
     * @param key the key
     * @param start the start
     * @param stop the stop
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZREMRANGEBYRANK)
    public abstract String zremrangebyrank(@RedisProtocolSupport.Key Buffer key, long start,
                                           long stop) throws Exception;

    /**
     * Remove all members in a sorted set within the given scores.
     *
     * @param key the key
     * @param min the min
     * @param max the max
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZREMRANGEBYSCORE)
    public abstract String zremrangebyscore(@RedisProtocolSupport.Key Buffer key, double min,
                                            double max) throws Exception;

    /**
     * Return a range of members in a sorted set, by index, with scores ordered from high to low.
     *
     * @param key the key
     * @param start the start
     * @param stop the stop
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZREVRANGE)
    public abstract String zrevrange(@RedisProtocolSupport.Key Buffer key, long start, long stop) throws Exception;

    /**
     * Return a range of members in a sorted set, by index, with scores ordered from high to low.
     *
     * @param key the key
     * @param start the start
     * @param stop the stop
     * @param withscores the withscores
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZREVRANGE)
    public abstract String zrevrange(@RedisProtocolSupport.Key Buffer key, long start, long stop,
                                     @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZrevrangeWithscores withscores) throws Exception;

    /**
     * Return a range of members in a sorted set, by lexicographical range, ordered from higher to lower strings.
     *
     * @param key the key
     * @param max the max
     * @param min the min
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZREVRANGEBYLEX)
    public abstract String zrevrangebylex(@RedisProtocolSupport.Key Buffer key, Buffer max,
                                          Buffer min) throws Exception;

    /**
     * Return a range of members in a sorted set, by lexicographical range, ordered from higher to lower strings.
     *
     * @param key the key
     * @param max the max
     * @param min the min
     * @param offsetCount the offsetCount
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZREVRANGEBYLEX)
    public abstract String zrevrangebylex(@RedisProtocolSupport.Key Buffer key, Buffer max, Buffer min,
                                          @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.LIMIT) @Nullable @RedisProtocolSupport.Tuple RedisProtocolSupport.OffsetCount offsetCount) throws Exception;

    /**
     * Return a range of members in a sorted set, by score, with scores ordered from high to low.
     *
     * @param key the key
     * @param max the max
     * @param min the min
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZREVRANGEBYSCORE)
    public abstract String zrevrangebyscore(@RedisProtocolSupport.Key Buffer key, double max,
                                            double min) throws Exception;

    /**
     * Return a range of members in a sorted set, by score, with scores ordered from high to low.
     *
     * @param key the key
     * @param max the max
     * @param min the min
     * @param withscores the withscores
     * @param offsetCount the offsetCount
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZREVRANGEBYSCORE)
    public abstract String zrevrangebyscore(@RedisProtocolSupport.Key Buffer key, double max, double min,
                                            @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZrevrangebyscoreWithscores withscores,
                                            @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.LIMIT) @Nullable @RedisProtocolSupport.Tuple RedisProtocolSupport.OffsetCount offsetCount) throws Exception;

    /**
     * Determine the index of a member in a sorted set, with scores ordered from high to low.
     *
     * @param key the key
     * @param member the member
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZREVRANK)
    public abstract String zrevrank(@RedisProtocolSupport.Key Buffer key, Buffer member) throws Exception;

    /**
     * Incrementally iterate sorted sets elements and associated scores.
     *
     * @param key the key
     * @param cursor the cursor
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZSCAN)
    public abstract String zscan(@RedisProtocolSupport.Key Buffer key, long cursor) throws Exception;

    /**
     * Incrementally iterate sorted sets elements and associated scores.
     *
     * @param key the key
     * @param cursor the cursor
     * @param matchPattern the matchPattern
     * @param count the count
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZSCAN)
    public abstract String zscan(@RedisProtocolSupport.Key Buffer key, long cursor,
                                 @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.MATCH) @Nullable Buffer matchPattern,
                                 @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.COUNT) @Nullable Long count) throws Exception;

    /**
     * Get the score associated with the given member in a sorted set.
     *
     * @param key the key
     * @param member the member
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZSCORE)
    public abstract String zscore(@RedisProtocolSupport.Key Buffer key, Buffer member) throws Exception;

    /**
     * Add multiple sorted sets and store the resulting sorted set in a new key.
     *
     * @param destination the destination
     * @param numkeys the numkeys
     * @param keys the keys
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZUNIONSTORE)
    public abstract String zunionstore(@RedisProtocolSupport.Key Buffer destination, long numkeys,
                                       @RedisProtocolSupport.Key Collection<Buffer> keys) throws Exception;

    /**
     * Add multiple sorted sets and store the resulting sorted set in a new key.
     *
     * @param destination the destination
     * @param numkeys the numkeys
     * @param keys the keys
     * @param weightses the weightses
     * @param aggregate the aggregate
     * @return a {@link String} result
     * @throws Exception if an exception occurs during the request processing.
     */
    @RedisProtocolSupport.Cmd(RedisProtocolSupport.Command.ZUNIONSTORE)
    public abstract String zunionstore(@RedisProtocolSupport.Key Buffer destination, long numkeys,
                                       @RedisProtocolSupport.Key Collection<Buffer> keys,
                                       @RedisProtocolSupport.SubCmd(RedisProtocolSupport.SubCommand.WEIGHTS) Collection<Long> weightses,
                                       @RedisProtocolSupport.Option @Nullable RedisProtocolSupport.ZunionstoreAggregate aggregate) throws Exception;
}
