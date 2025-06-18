package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class QuestionRepositoryTest {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    @DisplayName("findAll")
    void t1() {
        List<Question> questions = questionRepository.findAll();
        assertThat(questions).hasSize(2);

        Question question = questions.get(0);
        assertThat(question.getSubject()).isEqualTo("sbb가 무엇인가요?");
    }

    @Test
    @DisplayName("findById")
    void t2() {
        Question question = questionRepository.findById(1).get();
        assertThat(question.getSubject()).isEqualTo("sbb가 무엇인가요?");
    }

    @Test
    @DisplayName("findBySubject")
    void t3() {
        Question question = questionRepository.findBySubject("sbb가 무엇인가요?").get();
        assertThat(question.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("findBySubjectAndContent")
    void t4() {
        Question question = questionRepository.findBySubjectAndContent(
                "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.").get();
        assertThat(question.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("findBySubjectLike")
    void t5() {
        List<Question> questions = questionRepository.findBySubjectLike("sbb%");

        Question question = questions.get(0);
        assertThat(question.getSubject()).isEqualTo("sbb가 무엇인가요?");
    }

    @Test
    @DisplayName("수정")
    void t6() {
        Question question = questionRepository.findById(1).get();
        assertThat(question).isNotNull();

        question.setSubject("수정된 제목");
        questionRepository.save(question);

        Question foundQuestion = questionRepository.findBySubject("수정된 제목").get();
        assertThat(foundQuestion).isNotNull();
    }


    @Test
    @DisplayName("삭제")
    void t7() {
        assertThat(questionRepository.count()).isEqualTo(2);

        Question question = questionRepository.findById(1).get();
        questionRepository.delete(question);

        assertThat(questionRepository.count()).isEqualTo(1);
    }


    @Test
    @DisplayName("답변 생성")
    void t8() { // @OnetoMany 활용하지 않는 일반적인 방식
        Question question = this.questionRepository.findById(2).get();

        Answer answer = new Answer();
        answer.setContent("네 자동으로 생성됩니다.");
        answer.setQuestion(question);
        answer.setCreateDate(LocalDateTime.now());
        this.answerRepository.save(answer);

        assertThat(answer.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("답변 생성 v2 (OneToMany 사용)")
    // @Rollback(value = false)
    void t9() { // @OneToMany 활용하는 방법
        Question question = this.questionRepository.findById(2).get();

        int beforeCount = question.getAnswers().size();

        Answer newAnswer = question.addAnswer("네 자동으로 생성됩니다.");

        // 트랜잭션이 종료된 이후에 DB에 반영되기 때문에 현재는 0
        assertThat(newAnswer.getId()).isEqualTo(0);

        int afterCount = question.getAnswers().size();

        assertThat(afterCount).isEqualTo(beforeCount+1);
    }

    @Test
    @DisplayName("답변 조회")
    void t10() {
        Answer answer = this.answerRepository.findById(1).get();
        assertThat(answer.getQuestion().getId()).isEqualTo(2);
    }

    @Test
    @DisplayName("답변 조회 v2 (OneToMany)")
    void t11() {
        Question question = questionRepository.findById(2).get();

        List<Answer> answers = question.getAnswers();
        assertThat(answers).hasSize(1);

        Answer answer = answers.get(0);
        assertThat(answer.getContent()).isEqualTo("네 자동으로 생성됩니다.");
    }

    @Test
    @DisplayName("findById with OneToMany)")
    void t12() {
        // @OneToMany(fetch= FetchType.EAGER) -> question 가져올 때 answers도 채워옴
        // 디폴트는 LAZY 옵션 -> answers는 필요할 때 뒤늦게 채워서 사용
        Question question = questionRepository.findById(2).get();

        Answer answer1 = question.getAnswers().get(0);

        assertThat(answer1.getId()).isEqualTo(1);
    }
}
