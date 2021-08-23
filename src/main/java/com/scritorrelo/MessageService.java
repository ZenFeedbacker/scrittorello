package com.scritorrelo;

import com.scritorrelo.db.AudiosSqlQuery;
import com.scritorrelo.db.DatabaseManager;
import com.scritorrelo.zello.message.Message;
import com.scritorrelo.zello.message.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private DatabaseManager dbManager;

    List<Message> messages;

    public Page<Message> findPaginated(Pageable pageable) {

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Message> list;

        if (messages.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, messages.size());
            list = messages.subList(startItem, toIndex);
        }

     return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), messages.size());
    }

    @SuppressWarnings("unchecked")
    public <T extends  Message> List<T> getAll(MessageType type){

        DataSource dataSource = dbManager.getDataSource();

        MappingSqlQuery<T> sqlQuery;

        switch (type){
            case Audio:
                sqlQuery = (MappingSqlQuery<T>) new AudiosSqlQuery(dataSource);
                break;
            case Location:
            case Text:
            case Image:
            default:
                return new ArrayList<>();
        }

        return sqlQuery.execute();
    }
}
