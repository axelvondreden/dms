package com.dude.dms.backend.repositories;

import com.dude.dms.backend.data.docs.Doc;
import com.dude.dms.backend.data.docs.TextBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TextBlockRepository extends JpaRepository<TextBlock, Long> {

    List<TextBlock> findByDoc(Doc doc);

    long countByTextAndDoc(String text, Doc doc);

    long countByText(String text);

    List<TextBlock> findByTextAndDoc(String text, Doc doc);

    List<TextBlock> findByText(String text);
}