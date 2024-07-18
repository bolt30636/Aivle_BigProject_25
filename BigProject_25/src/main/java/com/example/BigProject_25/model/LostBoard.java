package com.example.BigProject_25.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lost_boards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LostBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int boardID;

    @Column(name = "img_filename", nullable = false)
    private String imgFilename;

    @Column(name = "board_name", nullable = false)
    private String boardName;

    @Column(name = "board_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime boardDate;

    @Column(name = "board_location", nullable = false)
    private String boardLocation;

    @Column(name = "description", length = 50, nullable = false)
    private String description;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "name", nullable = false)
    private String name;
}
