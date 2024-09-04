package com.readingtracker.boochive.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class PlainTextLengthValidator implements ConstraintValidator<PlainTextLength, String> {

    private int minLength;
    private int maxLength;

    @Override
    public void initialize(PlainTextLength constraintAnnotation) {
        this.minLength = constraintAnnotation.min();
        this.maxLength = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        Element parsed = Jsoup.parse(value, "UTF-8").body();

        List<String> blockTags = Arrays.asList(
                "h1", "h2", "h3", "h4", "h5", "h6", "p", "div", "ul", "ol", "li", "blockquote"
        );
        StringBuilder contents = new StringBuilder();
        List<Element> children = parsed.children();

        for (int i = 0; i < children.size(); i++) {
            Element element = children.get(i);
            contents.append(element.wholeText());

            if (blockTags.contains(element.tagName()) && i < children.size() - 1) {
                contents.append(" "); // 블록 요소 줄바꿈 공백 처리 (문자 길이 +1)
            }
        }

        String plainText = contents.toString();
        return plainText.length() <= maxLength && plainText.length() >= minLength;
    }
}
