package org.dorkmaster.library.jdbi.query;

import org.dorkmaster.library.model.Book;
import org.dorkmaster.library.model.Item;
import org.dorkmaster.library.model.Location;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public interface BookDao {
    @SqlQuery("select b.*, l.* from book b inner join location l on b.location_id = l.id where b.book_seller_id  = :id order by title")
    @Mapper(BookMapper.class)
    Collection<Item> findByBookSellerId(@Bind("id") String id);

    @SqlQuery("select b.*, l.* from book b inner join location l on b.location_id = l.id where l.id = :locationId order by title")
    @Mapper(BookMapper.class)
    Collection<Item> listByLocation(@Bind("locationId") String id);

    class BookMapper implements ResultSetMapper<Item> {
        @Override
        public Item map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
            return new Book()
                    .setAuthor(resultSet.getString("author"))
                    .setTitle(resultSet.getString("title"))
                    .setLocation(
                            new Location()
                                    .setId(resultSet.getString("l.id"))
                                    .setDescription(resultSet.getString("l.description"))
                    )
                    .setId(resultSet.getString("id"))
                    .setForeignId(resultSet.getString("book_seller_id"));

        }
    }


}
