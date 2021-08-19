package uk.nhs.digital.uec.api.util;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
@Slf4j
public class PostcodeUtils {

    private static final  String POSTCODE_REGEX="([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?))))\\s?[0-9][A-Za-z]{2})";
    private PostcodeUtils(){}

    public static List<String> validatePostCodes(List<String> postCodes) throws InvalidPostcodeException{
        List<String> validPostcodes = CollectionUtils.isNotEmpty(postCodes) ?
        postCodes.stream().filter(Objects::nonNull).map(PostcodeUtils::formatPostCodeWithoutSpace).filter(PostcodeUtils::validatePostCode).collect(Collectors.toList()) :
        Collections.emptyList();
        if(CollectionUtils.isNotEmpty(postCodes) && CollectionUtils.isEmpty(validPostcodes)){
            log.error("Invalid postcode/s entered : "+postCodes.stream().collect(Collectors.joining(",")));
            throw new InvalidPostcodeException();
        }
        return validPostcodes;
    }

    public static boolean validatePostCode(String postCode){
        postCode=formatPostCodeWithSpace(postCode);
        Pattern pattern = Pattern.compile(POSTCODE_REGEX);
        return pattern.matcher(postCode).matches();
    }

    public static List<String> formatPostCodes(List<String> postCodes){
        return postCodes.stream().map(PostcodeUtils::formatPostCodeWithoutSpace).collect(Collectors.toList());
    }

    public static String formatPostCodeWithoutSpace(String postCode){
        postCode =  postCode.length() <= 5 || !postCode.contains(StringUtils.SPACE) ? postCode.toUpperCase() :
        postCode.replaceAll("\\s+", "").toUpperCase();
        return postCode;
    }

    public static String formatPostCodeWithSpace(String postCode){
        postCode =  postCode.length() <= 5 || postCode.contains(StringUtils.SPACE) ? postCode.toUpperCase() :
        new StringJoiner("")
        .add(postCode.substring(0, postCode.length() - 3))
        .add(StringUtils.SPACE)
        .add(postCode.substring(postCode.length() - 3, postCode.length()))
        .toString()
        .toUpperCase();
        return postCode;
    }

}
