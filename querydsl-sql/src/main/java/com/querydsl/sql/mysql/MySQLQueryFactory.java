/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.sql.mysql;

import java.sql.Connection;

import javax.inject.Provider;

import com.querydsl.core.QueryFlag.Position;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.sql.*;
import com.querydsl.sql.dml.SQLInsertClause;

/**
 * MySQL specific implementation of SQLQueryFactory
 *
 * @author tiwe
 *
 */
public class MySQLQueryFactory extends AbstractSQLQueryFactory<MySQLQuery<?>> {

    public MySQLQueryFactory(Configuration configuration, Provider<Connection> connection) {
        super(configuration, connection);
    }

    public MySQLQueryFactory(Provider<Connection> connection) {
        this(new Configuration(new MySQLTemplates()), connection);
    }

    public MySQLQueryFactory(SQLTemplates templates, Provider<Connection> connection) {
        this(new Configuration(templates), connection);
    }

    /**
     * Create a INSERT IGNORE INTO clause
     *
     * @param entity table to insert to
     * @return insert clause
     */
    public SQLInsertClause insertIgnore(RelationalPath<?> entity) {
        SQLInsertClause insert = insert(entity);
        insert.addFlag(Position.START_OVERRIDE, "insert ignore into ");
        return insert;
    }

    /**
     * Create a INSERT ... ON DUPLICATE KEY UPDATE clause
     *
     * @param entity table to insert to
     * @param clause clause
     * @return insert clause
     */
    public SQLInsertClause insertOnDuplicateKeyUpdate(RelationalPath<?> entity, String clause) {
        SQLInsertClause insert = insert(entity);
        insert.addFlag(Position.END, " on duplicate key update " + clause);
        return insert;
    }

    /**
     * Create a INSERT ... ON DUPLICATE KEY UPDATE clause
     *
     * @param entity table to insert to
     * @param clause clause
     * @return insert clause
     */
    public SQLInsertClause insertOnDuplicateKeyUpdate(RelationalPath<?> entity, Expression<?> clause) {
        SQLInsertClause insert = insert(entity);
        insert.addFlag(Position.END, ExpressionUtils.template(String.class, " on duplicate key update {0}", clause));
        return insert;
    }

    @Override
    public MySQLQuery<?> query() {
        return new MySQLQuery<Void>(connection, configuration);
    }

    public MySQLReplaceClause replace(RelationalPath<?> entity) {
        return new MySQLReplaceClause(connection.get(), configuration, entity);
    }

}
