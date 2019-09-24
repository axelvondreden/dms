package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.Doc;
import com.dude.dms.backend.data.entity.User;
import com.dude.dms.backend.repositories.DocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

@Service
public class DocService implements CrudService<Doc> {

    private final DocRepository docRepository;

    @Autowired
    public DocService(DocRepository docRepository) {
        this.docRepository = docRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public Doc saveDoc(User currentUser, Long id, BiConsumer<User, Doc> docFiller) {
        Doc doc = id == null ? new Doc(currentUser) : load(id);
        docFiller.accept(currentUser, doc);
        return docRepository.save(doc);
    }

    @Transactional(rollbackOn = Exception.class)
    public Doc saveDoc(Doc doc) {
        return docRepository.save(doc);
    }

    @Transactional(rollbackOn = Exception.class)
    public Doc addComment(User currentUser, Doc doc, String comment) {
        //doc.addHistoryItem(currentUser, comment);
        return docRepository.save(doc);
    }

    public Page<Doc> findAnyMatchingAfterUploadDate(Optional<String> optionalFilter, Optional<LocalDate> optionalFilterDate, Pageable pageable) {
        if (optionalFilter.isPresent() && !optionalFilter.get().isEmpty()) {
            if (optionalFilterDate.isPresent()) {
                //return docRepository.findByCustomerFullNameContainingIgnoreCaseAndDueDateAfter(optionalFilter.get(), optionalFilterDate.get(), pageable);
            } else {
                //return docRepository.findByCustomerFullNameContainingIgnoreCase(optionalFilter.get(), pageable);
            }
        } else {
            if (optionalFilterDate.isPresent()) {
                return docRepository.findByUploadDateAfter(optionalFilterDate.get(), pageable);
            } else {
                return docRepository.findAll(pageable);
            }
        }
        return null;
    }

    public long countAnyMatchingAfterUploadDate(Optional<String> optionalFilter, Optional<LocalDate> optionalFilterDate) {
        if (optionalFilter.isPresent() && optionalFilterDate.isPresent()) {
            //return docRepository.countByCustomerFullNameContainingIgnoreCaseAndDueDateAfter(optionalFilter.get(), optionalFilterDate.get());
        } else if (optionalFilter.isPresent()) {
            //return docRepository.countByCustomerFullNameContainingIgnoreCase(optionalFilter.get());
        } else {
            return optionalFilterDate.map(docRepository::countByUploadDateAfter).orElseGet(docRepository::count);
        }
        return 0;
    }

    private List<Number> getDocsPerDay(int month, int year) {
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        return flattenAndReplaceMissingWithNull(daysInMonth, docRepository.countPerDay(year, month));
    }

    private List<Number> getDocsPerMonth(int year) {
        return flattenAndReplaceMissingWithNull(12, docRepository.countPerMonth(year));
    }

    private static List<Number> flattenAndReplaceMissingWithNull(int length, Iterable<Object[]> list) {
        List<Number> counts = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            counts.add(null);
        }

        for (Object[] result : list) {
            counts.set((Integer) result[0] - 1, (Number) result[1]);
        }
        return counts;
    }

    @Override
    public JpaRepository<Doc, Long> getRepository() {
        return docRepository;
    }

    @Transactional
    public Doc createNew(User currentUser) {
        Doc doc = new Doc(currentUser);
        doc.setUploadDate(LocalDateTime.now());
        return doc;
    }

    public List<Doc> findAll() {
        return docRepository.findAll();
    }
}
