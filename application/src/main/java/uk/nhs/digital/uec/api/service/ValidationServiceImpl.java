package uk.nhs.digital.uec.api.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.ErrorMessageEnum;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;

@Component
@Slf4j
public class ValidationServiceImpl implements ValidationService {

  private static final String POSTCODE_REGEX =
      "([Gg][Ii][Rr]"
          + " 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?))))\\s?[0-9][A-Za-z]{2})";

  @Override
  public List<String> validatePostCodes(List<String> postCodes)
      throws InvalidPostcodeException, NotFoundException {
    log.info("Validating postcode entries: {}", postCodes);
    List<String> validPostcodes =
        CollectionUtils.isNotEmpty(postCodes)
            ? postCodes.stream()
                .filter(Objects::nonNull)
                .map(this::formatPostCodeWithoutSpace)
                .filter(this::validatePostCode)
                .collect(Collectors.toList())
            : Collections.emptyList();
    if (CollectionUtils.isNotEmpty(postCodes) && CollectionUtils.isEmpty(validPostcodes)) {
      log.error(
          "Invalid postcode/s entered : " + postCodes.stream().collect(Collectors.joining(",")));
      throw new InvalidPostcodeException(ErrorMessageEnum.INVALID_POSTCODE.getMessage());
    }
    return validPostcodes;
  }

  @Override
  public boolean validatePostCode(String postCode) {
    postCode = formatPostCodeWithSpace(postCode);
    Pattern pattern = Pattern.compile(POSTCODE_REGEX);
    return pattern.matcher(postCode).matches();
  }

  @Override
  public List<PostcodeMapping> validateAndReturn(List<PostcodeMapping> location)
      throws NotFoundException {
    if (CollectionUtils.isEmpty(location)) {
      throw new NotFoundException(ErrorMessageEnum.NO_LOCATION_FOUND.getMessage());
    }
    return location;
  }

  public String formatPostCodeWithoutSpace(String postCode) {
    postCode =
        postCode.length() <= 5 || !postCode.contains(StringUtils.SPACE)
            ? postCode.toUpperCase()
            : postCode.replaceAll("\\s+", "").toUpperCase();
    return postCode;
  }

  public static String formatPostCodeWithSpace(String postCode) {
    postCode =
        postCode.length() <= 5 || postCode.contains(StringUtils.SPACE)
            ? postCode.toUpperCase()
            : new StringJoiner("")
                .add(postCode.substring(0, postCode.length() - 3))
                .add(StringUtils.SPACE)
                .add(postCode.substring(postCode.length() - 3, postCode.length()))
                .toString()
                .toUpperCase();
    return postCode;
  }
}
