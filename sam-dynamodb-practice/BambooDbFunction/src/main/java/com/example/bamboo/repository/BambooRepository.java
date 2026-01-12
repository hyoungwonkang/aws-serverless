package com.example.bamboo.repository;

import com.example.bamboo.entity.Bamboo;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

public class BambooRepository {
    private final DynamoDbTable<Bamboo> table;
}