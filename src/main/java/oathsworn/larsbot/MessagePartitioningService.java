package oathsworn.larsbot;

import com.google.common.collect.ImmutableList;

interface MessagePartitioningService {
    ImmutableList<String> getMessageParts(String message);
}
