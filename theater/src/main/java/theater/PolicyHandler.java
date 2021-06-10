package theater;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import theater.config.kafka.KafkaProcessor;

@Service
public class PolicyHandler {
    @Autowired
    MovieSeatRepository movieSeatRepository;
    @Autowired
    MovieRepository movieRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverApproved_SeatReserve(@Payload Approved approved) throws JsonProcessingException {

        if (!approved.validate())
            return;

        System.out.println("\n\n##### listener Seatreserve : " + approved.toJson() + "\n\n");

        /**
         * 좌석예약 등록
         */
        MovieSeat movieSeat = new MovieSeat();
        movieSeat.setBookId(approved.getBookId());
        movieSeat.setStatus("Reserved");

        /**$#@!.20210523.조현준
         * -----------------------------------------------------
         * - 좌석 등록 시 screenId 필요
         * - movie 테이블에서 screenId를 찾아서 SET
         * -----------------------------------------------------
         */
        Movie myMovie = movieRepository.findByMovieId(approved.getMovieId().toUpperCase());

        movieSeat.setScreenId(myMovie.getScreenId());
        movieSeat.setSeatId(approved.getSeatId());

        movieSeatRepository.save(movieSeat);
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCanceled_Seatcancel(@Payload PaymentCanceled paymentCanceled) {

        if (!paymentCanceled.validate())
            return;

        System.out.println("\n\n##### listener Seatcancel : " + paymentCanceled.toJson() + "\n\n");

        /**
         * 죄석예약 취소
         */
        /**$#@!.20210523.조현준
         * -----------------------------------------------------
         * - 좌석예약 취소 시 데이터 삭제가 아닌 status 변경 필요함
         * - update를 위해 원데이터 검색 후 변경
         * -----------------------------------------------------
         * MovieSeat movieSeat = new MovieSeat();
         * movieSeat.setBookId(paymentCanceled.getBookId());
         * movieSeatRepository.delete(movieSeat);
         */
        MovieSeat movieSeat = movieSeatRepository.findByBookId(paymentCanceled.getBookId());
        movieSeat.setStatus("canceled");
        movieSeatRepository.save(movieSeat);
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverMovieRegistered_RegisteMovie(@Payload MovieRegistered movieRegistered) {

        if (!movieRegistered.validate())
            return;

        System.out.println("\n\n##### listener RegisteMovie : " + movieRegistered.toJson() + "\n\n");

        /**
         * 영화등록
         */
        Movie movie = new Movie();
        movie.setMovieId(movieRegistered.getMovieId());
        movie.setScreenId(movieRegistered.getTitle() + "_상영관");
        movieRepository.save(movie);
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {

    }

}
