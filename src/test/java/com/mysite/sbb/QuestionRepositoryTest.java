package com.mysite.sbb;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
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
    @Transactional
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
    @Transactional
    void t7() {
        assertThat(questionRepository.count()).isEqualTo(2);

        Question question = questionRepository.findById(1).get();
        questionRepository.delete(question);

        assertThat(questionRepository.count()).isEqualTo(1);
    }


    @Test
    @DisplayName("답변 생성")
    @Transactional
    void t8() { // @OnetoMany 활용하지 않는 일반적인 방식
        Question question = this.questionRepository.findById(2).get();

        Answer answer = new Answer();
        answer.setContent("네 자동으로 생성됩니다.");
        answer.setQuestion(question);
        answer.setCreateDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    @Test
    @DisplayName("답변 생성 v2")
    @Transactional
    @Rollback(value = false)
    void t9() { // @OneToMany 활용하는 방법
        Question question = this.questionRepository.findById(2).get();

        question.addAnswer("네 자동으로 생성됩니다.");

        assertThat(question.getAnswers()).hasSize(1);
    }
/*
    @Test
    @DisplayName("")
    void t10() {
        Optional<Answer> answers = this.answerRepository.findById(1);
        assertThat(answers.isPresent()).isTrue();
        Answer answer = answers.get();
        assertThat(answer.getQuestion().getId()).isEqualTo(2);
    }

/*
    @Transactional
    @Test
    @DisplayName("")
    void t10() {
        Optional<Question> questions = this.questionRepository.findById(2);
        assertThat(questions.isPresent()).isTrue();
        Question question = questions.get();

        List<Answer> answers = question.getAnswers();

        assertThat(answers.size()).isEqualTo(1);
        assertThat( answers.get(0).getContent()).isEqualTo("네 자동으로 생성됩니다.");
    }
     */

}
