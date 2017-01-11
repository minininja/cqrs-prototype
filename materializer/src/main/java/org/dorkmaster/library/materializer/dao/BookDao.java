package org.dorkmaster.library.materializer.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface BookDao {
    @SqlQuery("update book set location = :locationId where bookId = :bookId")
    int updateLocation(@Bind("bookId") String bookId, @Bind("locationId") String locationId);

    @SqlUpdate(
            "insert into book (id, book_seller_id, title, author, location_id) " +
            "values (:id, :bookSellerId, :title, :author, :locationId)"
    )
    int addBook(
            @Bind("id") String id,
            @Bind("bookSellerId") String bookSellerId,
            @Bind("title") String title,
            @Bind("author") String author,
            @Bind("locationId") String locationId
    );

    @SqlUpdate("delete from book where id = :id")
    int deleteBook(@Bind("id") String id);
}
