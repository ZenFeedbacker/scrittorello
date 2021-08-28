package com.scritorrelo;

import com.scritorrelo.db.AudiosSqlQuery;
import com.scritorrelo.db.DatabaseManager;
import com.scritorrelo.zello.message.Message;
import com.scritorrelo.zello.message.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private DatabaseManager dbManager;

    List<Message> messages;

    @SuppressWarnings("unchecked")
    public <T extends  Message> List<T> getAll(MessageType type){

        DataSource dataSource = dbManager.getDataSource();

        MappingSqlQuery<T> sqlQuery;

        switch (type){
            case AUDIO:
                sqlQuery = (MappingSqlQuery<T>) new AudiosSqlQuery(dataSource);
                break;
            case LOCATION:
            case TEXT:
            case IMAGE:
            default:
                return new ArrayList<>();
        }

        return sqlQuery.execute();
    }
}
