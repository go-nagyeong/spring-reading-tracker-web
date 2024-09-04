package com.readingtracker.boochive.config;

import com.readingtracker.boochive.domain.*;
import com.readingtracker.boochive.domain.ReadingNote;
import com.readingtracker.boochive.util.ResourceAccessUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceAccessUtilConfig {

    @Bean
    public ResourceAccessUtil<Review> reviewAccessUtil() {
        ResourceAccessUtil<Review> util = new ResourceAccessUtil<>();
        util.init(Review.class);
        return util;
    }

    @Bean
    public ResourceAccessUtil<BookCollection> bookCollectionAccessUtil() {
        ResourceAccessUtil<BookCollection> util = new ResourceAccessUtil<>();
        util.init(BookCollection.class);
        return util;
    }

    @Bean
    public ResourceAccessUtil<ReadingRecord> readingRecordAccessUtil() {
        ResourceAccessUtil<ReadingRecord> util = new ResourceAccessUtil<>();
        util.init(ReadingRecord.class);
        return util;
    }

    @Bean
    public ResourceAccessUtil<ReadingBook> readingBookAccessUtil() {
        ResourceAccessUtil<ReadingBook> util = new ResourceAccessUtil<>();
        util.init(ReadingBook.class);
        return util;
    }

    @Bean
    public ResourceAccessUtil<PurchaseHistory> purchaseHistoryAccessUtil() {
        ResourceAccessUtil<PurchaseHistory> util = new ResourceAccessUtil<>();
        util.init(PurchaseHistory.class);
        return util;
    }

    @Bean
    public ResourceAccessUtil<RentalHistory> rentalHistoryAccessUtil() {
        ResourceAccessUtil<RentalHistory> util = new ResourceAccessUtil<>();
        util.init(RentalHistory.class);
        return util;
    }

    @Bean
    public ResourceAccessUtil<UserConfig> userConfigAccessUtil() {
        ResourceAccessUtil<UserConfig> util = new ResourceAccessUtil<>();
        util.init(UserConfig.class);
        return util;
    }

    @Bean
    public ResourceAccessUtil<ReadingNote> readingNoteAccessUtil() {
        ResourceAccessUtil<ReadingNote> util = new ResourceAccessUtil<>();
        util.init(ReadingNote.class);
        return util;
    }
}
