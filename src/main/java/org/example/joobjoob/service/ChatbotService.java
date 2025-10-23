package org.example.joobjoob.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.joobjoob.entity.Course;
import org.example.joobjoob.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // 로깅용
public class ChatbotService {

    private final OpenAiService openAiService;
    private final CourseRepository courseRepository;

    // 키워드 기반의 간단한 의도 파악
    public String handleQuery(String query) {
        if (query.contains("추천") || query.contains("관련된")) {
            log.info("추천 질문 처리: {}", query);
            return recommendCoursesByTopic(query);
        } else if (query.contains("시간") && (query.contains("중복") || query.contains("겹치지 않") || query.contains("겹치지않"))) {
            log.info("시간 중복 질문 처리: {}", query);
            return findNonConflictingCourses(query);
        } else {
            log.warn("알 수 없는 질문 유형: {}", query);
            return "죄송합니다. 이해할 수 없는 질문입니다. '과목 추천' 또는 '시간 중복 확인' 형식으로 질문해주세요.";
        }
    }

    // --- 주제 기반 추천 로직 ---
    private String recommendCoursesByTopic(String query) {
        // 1. 관련 강의 조회 (예: 모든 강의 또는 '교양' 필터링)
        // 여기서는 모든 강의를 가져옵니다. 필요에 따라 필터링하세요.
        List<Course> allCourses = courseRepository.findAll();
        if (allCourses.isEmpty()) {
            return "추천할 강의 목록이 없습니다.";
        }

        // 2. OpenAI에 보낼 문맥(Context) 준비 (필요시 크기 제한)
        String courseContext = allCourses.stream()
                .map(course -> String.format("과목명: %s (설명: %s)",
                        course.getTitle(),
                        course.getDescription() != null ? course.getDescription().substring(0, Math.min(course.getDescription().length(), 50)) + "..." : "설명 없음"))
                .collect(Collectors.joining("\n"));

        // 3. 프롬프트 구성
        String prompt = String.format(
                "다음 강의 목록 중에서 사용자의 질문과 관련된 과목 3개만 추천해주세요. 과목명만 목록으로 알려주세요.\n\n사용자 질문: \"%s\"\n\n강의 목록:\n%s",
                query, courseContext
        );

        log.debug("OpenAI 전송 프롬프트:\n{}", prompt);

        // 4. OpenAI API 호출
        try {
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo") // 또는 최신 모델 사용 (예: gpt-4)
                    .messages(List.of(
                            new ChatMessage(ChatMessageRole.SYSTEM.value(), "당신은 대학 강의를 추천하는 유용한 도우미입니다."),
                            new ChatMessage(ChatMessageRole.USER.value(), prompt)
                    ))
                    .maxTokens(150) // 필요에 따라 조절
                    .temperature(0.5) // 낮을수록 결정적인 결과
                    .build();

            String response = openAiService.createChatCompletion(completionRequest)
                    .getChoices().get(0).getMessage().getContent();

            log.info("OpenAI 응답 수신: {}", response);
            return "추천 과목:\n" + response.trim();

        } catch (Exception e) {
            log.error("OpenAI API 호출 오류: {}", e.getMessage(), e);
            return "죄송합니다. 추천 과정에서 오류가 발생했습니다.";
        }
    }

    // --- 시간 중복 확인 로직 ---
    private String findNonConflictingCourses(String query) {
        // 1. 대상 과목 코드/이름 추출 (데모용 간단 추출)
        // 더 강력한 방법은 정규식 또는 자연어 처리 사용
        String targetCourseCode = extractCourseCode(query); // 이 헬퍼 메소드 구현 필요
        if (targetCourseCode == null) {
            return "시간 비교를 원하는 기준 과목의 과목 코드를 정확히 알려주세요 (예: 'IT101').";
        }

        Optional<Course> targetCourseOpt = courseRepository.findAll().stream()
                .filter(c -> targetCourseCode.equalsIgnoreCase(c.getCode()))
                .findFirst();

        if (targetCourseOpt.isEmpty()) {
            return "과목 코드 '" + targetCourseCode + "'에 해당하는 강의를 찾을 수 없습니다.";
        }

        Course targetCourse = targetCourseOpt.get();
        Set<TimeSlot> targetSlots = parseSchedule(targetCourse.getScheduleString());

        if (targetSlots.isEmpty()) {
            return "'" + targetCourse.getTitle() + "' 과목의 시간표 정보가 없거나 잘못되었습니다.";
        }

        // 2. 다른 모든 과목 조회
        List<Course> otherCourses = courseRepository.findAll().stream()
                .filter(c -> !c.getId().equals(targetCourse.getId())) // 대상 과목 제외
                .toList();

        // 3. 겹치지 않는 과목 필터링
        List<String> nonConflictingCourseTitles = new ArrayList<>();
        for (Course otherCourse : otherCourses) {
            Set<TimeSlot> otherSlots = parseSchedule(otherCourse.getScheduleString());
            if (!doSchedulesConflict(targetSlots, otherSlots)) {
                nonConflictingCourseTitles.add(otherCourse.getTitle() + " (" + otherCourse.getCode() + ")");
            }
        }

        // 4. 결과 포맷팅
        if (nonConflictingCourseTitles.isEmpty()) {
            return "'" + targetCourse.getTitle() + "' 과목과 시간이 겹치지 않는 다른 과목이 없습니다.";
        } else {
            return "'" + targetCourse.getTitle() + "' 과목과 시간이 겹치지 않는 과목:\n"
                    + String.join("\n", nonConflictingCourseTitles);
        }
    }

    // --- 시간 중복 확인용 헬퍼 메소드 ---

    // 간단한 과목 코드 추출 (필요시 정규식 수정)
    private String extractCourseCode(String query) {
        Pattern pattern = Pattern.compile("([A-Za-z]+[0-9]+)"); // IT101, GE101 같은 패턴 매칭
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            return matcher.group(1).toUpperCase();
        }
        return null; // 코드 미발견 시 이름 추출 시도 가능
    }

    // 시간 슬롯을 나타내는 간단한 레코드
    private record TimeSlot(String day, int period) {}

    // 시간표 문자열 파싱 (예: "Mon 1,2/Wed 3,4")
    private Set<TimeSlot> parseSchedule(String scheduleString) {
        if (scheduleString == null || scheduleString.isBlank()) {
            return Collections.emptySet();
        }
        Set<TimeSlot> slots = new HashSet<>();
        try {
            String[] dayParts = scheduleString.split("/");
            for (String part : dayParts) {
                part = part.trim();
                String[] dayAndPeriods = part.split("\\s+", 2); // 요일과 교시 분리 (예: "Mon", "1,2")
                if (dayAndPeriods.length == 2) {
                    String day = dayAndPeriods[0].trim().toUpperCase(); // 예: MON
                    String[] periods = dayAndPeriods[1].split(",");
                    for (String periodStr : periods) {
                        slots.add(new TimeSlot(day, Integer.parseInt(periodStr.trim())));
                    }
                }
            }
        } catch (Exception e) {
            log.error("시간표 문자열 파싱 실패: '{}'. 오류: {}", scheduleString, e.getMessage());
            return Collections.emptySet(); // 파싱 오류 시 빈 Set 반환
        }
        return slots;
    }

    // 두 시간표 집합이 겹치는지 확인
    private boolean doSchedulesConflict(Set<TimeSlot> slots1, Set<TimeSlot> slots2) {
        if (slots1.isEmpty() || slots2.isEmpty()) {
            return false; // 시간표 정보가 없으면 겹치지 않음
        }
        // 공통 요소(교집합)가 있는지 확인
        Set<TimeSlot> intersection = new HashSet<>(slots1);
        intersection.retainAll(slots2);
        return !intersection.isEmpty(); // 교집합이 비어있지 않으면 겹침
    }
}