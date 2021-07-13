package com.scritorrelo;

import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import lombok.Getter;

public class DatabaseSchema {

    @Getter
    private static final DatabaseSchema INSTANCE = new DatabaseSchema();

    DbSchema SCHEMA;

    public DbTable TEXT_TABLE;
    public DbTable IMAGE_TABLE;
    public DbTable LOCATION_TABLE;
    public DbTable AUDIO_TABLE;

    public DbColumn UUID_TEXT;
    public DbColumn ID_TEXT;
    public DbColumn CHANNEL_TEXT;
    public DbColumn FOR_USER_TEXT;
    public DbColumn FROM_USER_TEXT;
    public DbColumn TIMESTAMP_TEXT;
    public DbColumn TEXT_TEXT;

    public DbColumn UUID_IMAGE;
    public DbColumn ID_IMAGE;
    public DbColumn CHANNEL_IMAGE;
    public DbColumn FOR_USER_IMAGE;
    public DbColumn FROM_USER_IMAGE;
    public DbColumn TIMESTAMP_IMAGE;
    public DbColumn TYPE_IMAGE;
    public DbColumn SOURCE_IMAGE;
    public DbColumn HEIGHT_IMAGE;
    public DbColumn WIDTH_IMAGE;
    public DbColumn IMAGE_FILE_IMAGE;

    public DbColumn UUID_LOCATION;
    public DbColumn ID_LOCATION;
    public DbColumn CHANNEL_LOCATION;
    public DbColumn FOR_USER_LOCATION;
    public DbColumn FROM_USER_LOCATION;
    public DbColumn TIMESTAMP_LOCATION;
    public DbColumn LONGITUDE_LOCATION;
    public DbColumn LATITUDE_LOCATION;
    public DbColumn ACCURACY_LOCATION;
    public DbColumn FORMATTED_ADDRESS_LOCATION;

    public DbColumn UUID_AUDIO;
    public DbColumn ID_AUDIO;
    public DbColumn CHANNEL_AUDIO;
    public DbColumn FOR_USER_AUDIO;
    public DbColumn FROM_USER_AUDIO;
    public DbColumn TIMESTAMP_AUDIO;
    public DbColumn TYPE_AUDIO;
    public DbColumn CODEC_AUDIO;
    public DbColumn CODEC_HEADER_AUDIO;
    public DbColumn PACKET_DURATION_AUDIO;
    public DbColumn AUDIO_FILE_AUDIO;

    private DatabaseSchema() {
        SCHEMA= new DbSpec().addDefaultSchema();

        TEXT_TABLE = SCHEMA.addTable("text");
        UUID_TEXT = TEXT_TABLE.addColumn("uuid", "uuid", null);
        ID_TEXT = TEXT_TABLE.addColumn("id", "int", null);
        CHANNEL_TEXT = TEXT_TABLE.addColumn("channel", "varchar", 255);
        FOR_USER_TEXT = TEXT_TABLE.addColumn("for_user", "varchar", 255);
        FROM_USER_TEXT = TEXT_TABLE.addColumn("from_user", "varchar", 255);
        TIMESTAMP_TEXT = TEXT_TABLE.addColumn("timestamp", "timestamp", null);
        TEXT_TEXT = TEXT_TABLE.addColumn("text", "varchar", 255);
    }
}
