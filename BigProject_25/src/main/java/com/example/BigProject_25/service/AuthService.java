package com.example.BigProject_25.service;

import com.example.BigProject_25.model.User;
import com.example.BigProject_25.model.VerificationToken;
import com.example.BigProject_25.repository.UserRepository;
import com.example.BigProject_25.repository.VerificationTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthService {
    private String provider;
    private String token;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CaptchaService captchaService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-secret}")
    private String googleClientSecret;

    @Value("${google.redirect-uri}")
    private String googleRedirectUri;

//    @Value("${naver.client-id}")
//    private String naverClientId;
//
//    @Value("${naver.client-secret}")
//    private String naverClientSecret;
//
//    @Value("${naver.redirect-uri}")
//    private String naverRedirectUri;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public boolean socialLogin(String provider, String token) {
        this.provider = provider;
        this.token = token;

        switch (provider.toLowerCase()) {
            case "kakao":
                return kakaoLogin(token);
            case "google":
                return googleLogin(token);
//            case "naver":
//                return naverLogin(token);
            default:
                return false;
        }
    }
    public boolean kakaoLogin(String token) {
        try {
            String url = "https://kapi.kakao.com/v2/user/me";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                // 로그 출력 및 오류 처리
                System.out.println("Unauthorized - Invalid token or insufficient permissions");
                return false;
            }

            String result = response.getBody();
            System.out.println("Response from Kakao API: " + result);

            JSONObject jsonObj = new JSONObject(result);

            if (!jsonObj.has("kakao_account")) {
                throw new JSONException("kakao_account not found in the response");
            }

            JSONObject kakaoAccount = jsonObj.getJSONObject("kakao_account");


            User user = new User();
            if (kakaoAccount.has("profile") && kakaoAccount.getJSONObject("profile").has("nickname")) {
                user.setName(kakaoAccount.getJSONObject("profile").getString("nickname"));
            } else {
                throw new JSONException("nickname not found in the response");
            }
            user.setEmailVerified(true);
            userRepository.save(user);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getKakaoAccessToken(String code) {
        try {
            String url = "https://kauth.kakao.com/oauth/token";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("client_id", kakaoRestApiKey);
            body.add("redirect_uri", kakaoRedirectUri);
            body.add("code", code);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            String result = response.getBody();

            JSONObject jsonObj = new JSONObject(result);
            return jsonObj.getString("access_token");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private boolean googleLogin(String token) {
        try {
            String url = "https://www.googleapis.com/oauth2/v3/userinfo";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String result = response.getBody();

            JSONObject jsonObj = new JSONObject(result);

            // 사용자 이메일로 이미 등록된 사용자가 있는지 확인
            User existingUser = userRepository.findByEmail(jsonObj.getString("email"));
            User user;
            if (existingUser == null) {
                user = new User();
                user.setName(jsonObj.getString("name"));
                user.setEmail(jsonObj.getString("email"));
                user.setEmailVerified(true);
                userRepository.save(user);
            } else {
                user = existingUser;
                if (!user.isEmailVerified()) {
                    user.setEmailVerified(true);
                    userRepository.save(user);
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getGoogleAccessToken(String code) {
        try {
            String url = "https://oauth2.googleapis.com/token";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("client_id", googleClientId);
            body.add("client_secret", googleClientSecret);
            body.add("redirect_uri", googleRedirectUri);
            body.add("code", code);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            String result = response.getBody();

            JSONObject jsonObj = new JSONObject(result);
            return jsonObj.getString("access_token");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
//
//    private boolean naverLogin(String token) {
//        try {
//            String url = "https://openapi.naver.com/v1/nid/me";
//            RestTemplate restTemplate = new RestTemplate();
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Bearer " + token);
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//            String result = response.getBody();
//
//            JSONObject jsonObj = new JSONObject(result);
//            JSONObject responseObj = jsonObj.getJSONObject("response");
//
//            // 사용자 이메일로 이미 등록된 사용자가 있는지 확인
//            User existingUser = userRepository.findByEmail(responseObj.getString("email"));
//            User user;
//            if (existingUser == null) {
//                user = new User();
//                user.setName(responseObj.getString("name"));
//                user.setEmail(responseObj.getString("email"));
//                user.setPhoneNum(responseObj.getString("phoneNum"));
//                user.setEmailVerified(true);
//                userRepository.save(user);
//            } else {
//                user = existingUser;
//                if (!user.isEmailVerified()) {
//                    user.setEmailVerified(true);
//                    userRepository.save(user);
//                }
//            }
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//    public String getNaverAccessToken(String code) {
//        try {
//            String url = "https://nid.naver.com/oauth2.0/token";
//            RestTemplate restTemplate = new RestTemplate();
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//            body.add("grant_type", "authorization_code");
//            body.add("client_id", naverClientId);
//            body.add("client_secret", naverClientSecret);
//            body.add("redirect_uri", naverRedirectUri);
//            body.add("code", code);
//            body.add("state", "random_state");
//
//            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//            String result = response.getBody();
//
//            JSONObject jsonObj = new JSONObject(result);
//            return jsonObj.getString("access_token");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public boolean login(String userID, String password, String captchaResponse) {
        boolean isCaptchaValid = true;  // 캡챠 검증을 임시로 통과시키기 위해 true로 설정
        if (!isCaptchaValid) {
            // 캡챠 검증 실패
            return false;
        }

        // 사용자 ID를 기반으로 사용자를 인증하고, 성공하면 jwt 토큰을 생성해서 this.token에 저장함
        User user = userRepository.findByUserID(userID);
        if (user != null && !user.isAccountLocked()) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                // 로그인 성공
                user.setLoginAttempts(0);
                userRepository.save(user);

                // JWT 토큰 생성
                this.token = generateJwtToken(user);
                return true;
            } else {
                // 비밀번호 불일치
                user.setLoginAttempts(user.getLoginAttempts() + 1);
                if (user.getLoginAttempts() >= 5) {
                    user.setAccountLocked(true);
                }
                userRepository.save(user);
                return false;
            }
        }

        return false;
    }

    public String getUserIdByEmailAndName(String email, String name) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getName().equals(name)) {
            return user.getUserID();
        }
        return null;
    }

    @Transactional
    public boolean signup(String name, String email, String userID, String phoneNum, String password, String captchaResponse) {
        boolean isCaptchaValid = true;  // 캡챠 검증을 임시로 통과시키기 위해 true로 설정
        if (!isCaptchaValid) {
            // 캡챠 검증 실패
            return false;
        }

        if (userRepository.findByUserID(userID) != null) {
            // 이미 해당 userID를 가진 사용자가 존재함
            return false;
        }
        if (userRepository.findByEmail(email) != null) {
            // 이미 사용자가 존재함
            return false;
        }

        if (!isValidPassword(password)) {
            // 비밀번호가 유효하지 않음
            return false;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setUserID(userID);  // userID 설정 추가
        user.setPhoneNum(phoneNum);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserType("user");

        userRepository.save(user);
        String verificationToken = generateVerificationToken(user);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
        return true;
    }

    private boolean isValidPassword(String password) {
        // 비밀번호 규칙: 영문, 숫자, 특수문자 중 2종류 조합, 10~16자리
        String regex = "^(?=.*[a-zA-Z])(?=.*\\d|.*\\W).{10,16}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }


    // 비밀번호 재설정 요청 함수
    public boolean requestPasswordReset(String userID, String name) {
        User user = userRepository.findByUserID(userID);
        if (user != null && user.getName().equals(name)) {
            String tempPassword = generateTemporaryPassword();
            user.setPassword(passwordEncoder.encode(tempPassword));
            userRepository.save(user);
            emailService.sendTemporaryPasswordEmail(user.getEmail(), tempPassword);
            return true;
        }
        return false;
    }

    // 임시 비밀번호 생성 함수
    private String generateTemporaryPassword() {
        // 임시 비밀번호 생성 로직 (10자리 영문, 숫자 조합)
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder tempPassword = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int randomIndex = (int) (Math.random() * chars.length());
            tempPassword.append(chars.charAt(randomIndex));
        }
        return tempPassword.toString();
    }

    // 비밀번호 변경 함수
    public boolean changePassword(String userID, String oldPassword, String newPassword) {
        User user = userRepository.findByUserID(userID);
        if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
            if (isValidPassword(newPassword)) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user, new Date(System.currentTimeMillis() + 300000)); // 5분 만료
        tokenRepository.save(verificationToken);
        return token;
    }

    public boolean verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null || verificationToken.getExpiryDate().before(new Date())) {
            return false;
        }
        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken); // 인증이 완료된 토큰 삭제
        return true;
    }

    public String getNameFromToken() {
        if (this.token == null) {
            return null;
        }
        Claims claims = parseJwt(token);
        return claims.get("name", String.class);
    }

    public String getEmailFromToken() {
        if (this.token == null) {
            return null;
        }
        Claims claims = parseJwt(token);
        return claims.get("email", String.class);
    }

    public boolean logout() {
        // this.token을 null로 설정해서 로그아웃을 처리함
        this.token = null;
        return true;
    }

    private String generateJwtToken(User user) {
        // 사용자 정보를 바탕으로 JWT 토큰을 생성합니다.
        // 사용자 ID, 이름, 이메일을 클레임으로 포함합니다.
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    private Claims parseJwt(String token) {
        // 주어진 JWT 토큰을 파싱하여 클레임을 추출
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }
}
