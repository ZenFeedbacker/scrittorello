package com.scritorrelo.db;

import com.scritorrelo.zello.message.audio.Audio;
import org.springframework.jdbc.object.MappingSqlQuery;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class AudiosSqlQuery extends MappingSqlQuery<Audio> {

    public AudiosSqlQuery(DataSource ds) {

        super(ds, "SELECT * FROM AUDIO");
        compile();
    }

    @Override
    protected Audio mapRow(ResultSet rs, int i) throws SQLException {

        return Audio.builder()
                .uuid(rs.getObject(1, UUID.class))
                .id(rs.getObject(2,Integer.class))
                .timestamp(rs.getObject(3, LocalDateTime.class))
                .channel(rs.getObject(4, String.class))
                .fromUser(rs.getObject(5, String.class))
                .forUser(rs.getObject(6, String.class))
                .type(rs.getObject(7, String.class))
                .codec(rs.getObject(8, String.class))
                .codecHeader(rs.getObject(9,String.class))
                .packetDuration(rs.getObject(10, Integer.class))
                .build();
    }
}
