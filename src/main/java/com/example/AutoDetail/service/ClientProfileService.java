package com.example.AutoDetail.service;

import com.example.AutoDetail.dto.ClientProfileDTO;
import com.example.AutoDetail.entity.Car;
import com.example.AutoDetail.entity.CarDetail;
import com.example.AutoDetail.entity.Client;
import com.example.AutoDetail.repository.CarDetailRepository;
import com.example.AutoDetail.repository.CarRepository;
import com.example.AutoDetail.repository.ClientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ClientProfileService {

    private final ClientRepository clientRepository;
    private final CarRepository carRepository;
    private final CarDetailRepository carDetailRepository;
    private final PasswordEncoder passwordEncoder;

    // Регулярные выражения для валидации
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[\\+]?[0-9\\-\\(\\)\\s]{7,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");
    private static final Pattern VIN_PATTERN = Pattern.compile("^[A-HJ-NPR-Z0-9]{17}$");
    private static final Pattern ENGINE_CODE_PATTERN = Pattern.compile("^[A-Z0-9]{3,20}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яА-ЯёЁ\\s-]{2,50}$");

    public ClientProfileService(ClientRepository clientRepository,
                                CarRepository carRepository,
                                CarDetailRepository carDetailRepository,
                                PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.carRepository = carRepository;
        this.carDetailRepository = carDetailRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Client getClientProfile(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
    }

    @Transactional
    public Client updateClientProfile(Long clientId, ClientProfileDTO profileDTO) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        // Валидация данных профиля
        validateProfileData(profileDTO);

        boolean loginChanged = !client.getLogin().equals(profileDTO.getLogin());

        // Проверка уникальности логина
        if (loginChanged && clientRepository.existsByLogin(profileDTO.getLogin())) {
            throw new RuntimeException("Логин уже занят");
        }

        // Проверка уникальности телефона
        if (!client.getPhone().equals(profileDTO.getPhone()) &&
                clientRepository.existsByPhone(profileDTO.getPhone())) {
            throw new RuntimeException("Телефон уже занят");
        }

        // Проверка уникальности email (если email указан)
        if (profileDTO.getEmail() != null && !profileDTO.getEmail().trim().isEmpty()) {
            String newEmail = profileDTO.getEmail().trim();
            if (!newEmail.equals(client.getEmail())) {
                Optional<Client> existingClient = clientRepository.findByEmail(newEmail);
                if (existingClient.isPresent() && !existingClient.get().getId().equals(clientId)) {
                    throw new RuntimeException("Email уже используется другим пользователем");
                }
            }
        }

        // Сохраняем старый логин для проверки изменений
        String oldLogin = client.getLogin();

        // Обновление основных данных
        client.setName(profileDTO.getName());
        client.setSurname(profileDTO.getSurname());
        client.setPatronymic(profileDTO.getPatronymic());
        client.setPhone(profileDTO.getPhone());
        client.setLogin(profileDTO.getLogin());

        // Обновление email - если передан пустой или null, устанавливаем null
        if (profileDTO.getEmail() != null && !profileDTO.getEmail().trim().isEmpty()) {
            client.setEmail(profileDTO.getEmail().trim());
        } else {
            client.setEmail(null); // Удаляем email, если поле пустое
        }

        Client updatedClient = clientRepository.save(client);

        // Если логин изменился, обновляем контекст безопасности
        if (loginChanged) {
            updateSecurityContext(oldLogin, updatedClient);
        }

        return updatedClient;
    }

    // Валидация данных профиля
    private void validateProfileData(ClientProfileDTO profileDTO) {
        // Валидация имени
        if (profileDTO.getName() == null || profileDTO.getName().trim().isEmpty()) {
            throw new RuntimeException("Имя обязательно");
        }
        if (!NAME_PATTERN.matcher(profileDTO.getName()).matches()) {
            throw new RuntimeException("Имя должно содержать только буквы, пробелы и дефисы (2-50 символов)");
        }

        // Валидация фамилии
        if (profileDTO.getSurname() == null || profileDTO.getSurname().trim().isEmpty()) {
            throw new RuntimeException("Фамилия обязательна");
        }
        if (!NAME_PATTERN.matcher(profileDTO.getSurname()).matches()) {
            throw new RuntimeException("Фамилия должна содержать только буквы, пробелы и дефисы (2-50 символов)");
        }

        // Валидация отчества (необязательное поле)
        if (profileDTO.getPatronymic() != null && !profileDTO.getPatronymic().trim().isEmpty()) {
            if (!NAME_PATTERN.matcher(profileDTO.getPatronymic()).matches()) {
                throw new RuntimeException("Отчество должно содержать только буквы, пробелы и дефисы (2-50 символов)");
            }
        }

        // Валидация телефона
        if (profileDTO.getPhone() == null || profileDTO.getPhone().trim().isEmpty()) {
            throw new RuntimeException("Телефон обязателен");
        }
        if (!PHONE_PATTERN.matcher(profileDTO.getPhone()).matches()) {
            throw new RuntimeException("Неверный формат телефона");
        }

        // Валидация email (необязательное поле)
        if (profileDTO.getEmail() != null && !profileDTO.getEmail().trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(profileDTO.getEmail()).matches()) {
                throw new RuntimeException("Неверный формат email");
            }
        }

        // Валидация логина
        if (profileDTO.getLogin() == null || profileDTO.getLogin().trim().isEmpty()) {
            throw new RuntimeException("Логин обязателен");
        }
        if (!LOGIN_PATTERN.matcher(profileDTO.getLogin()).matches()) {
            throw new RuntimeException("Логин должен содержать только латинские буквы, цифры и подчеркивания (3-50 символов)");
        }
    }

    // Валидация данных автомобиля
    private void validateCarData(ClientProfileDTO carDTO) {
        // Валидация марки автомобиля
        if (carDTO.getCarBrand() == null || carDTO.getCarBrand().trim().isEmpty()) {
            throw new RuntimeException("Марка автомобиля обязательна");
        }
        if (carDTO.getCarBrand().length() < 2 || carDTO.getCarBrand().length() > 50) {
            throw new RuntimeException("Марка автомобиля должна быть от 2 до 50 символов");
        }

        // Валидация модели автомобиля
        if (carDTO.getCarModel() == null || carDTO.getCarModel().trim().isEmpty()) {
            throw new RuntimeException("Модель автомобиля обязательна");
        }
        if (carDTO.getCarModel().length() < 1 || carDTO.getCarModel().length() > 50) {
            throw new RuntimeException("Модель автомобиля должна быть от 1 до 50 символов");
        }

        // Валидация типа топлива
        if (carDTO.getFuelType() == null || carDTO.getFuelType().trim().isEmpty()) {
            throw new RuntimeException("Тип топлива обязателен");
        }
        if (carDTO.getFuelType().length() > 20) {
            throw new RuntimeException("Тип топлива не должен превышать 20 символов");
        }

        // Валидация кода двигателя
        if (carDTO.getEngineCode() == null || carDTO.getEngineCode().trim().isEmpty()) {
            throw new RuntimeException("Код двигателя обязателен");
        }
        if (!ENGINE_CODE_PATTERN.matcher(carDTO.getEngineCode()).matches()) {
            throw new RuntimeException("Код двигателя должен содержать только заглавные буквы и цифры (3-20 символов)");
        }

        // Валидация VIN кода
        if (carDTO.getVinCode() == null || carDTO.getVinCode().trim().isEmpty()) {
            throw new RuntimeException("VIN код обязателен");
        }
        if (!VIN_PATTERN.matcher(carDTO.getVinCode()).matches()) {
            throw new RuntimeException("Неверный формат VIN кода. Должен содержать 17 символов (буквы и цифры)");
        }
    }

    /**
     * Обновление контекста безопасности после смены логина
     */
    private void updateSecurityContext(String oldLogin, Client updatedClient) {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth != null && currentAuth.getName().equals(oldLogin)) {
            // Создаем новую аутентификацию с обновленными данными
            UsernamePasswordAuthenticationToken newAuthentication =
                    new UsernamePasswordAuthenticationToken(
                            updatedClient.getLogin(), // новый логин
                            currentAuth.getCredentials(),
                            currentAuth.getAuthorities()
                    );
            newAuthentication.setDetails(currentAuth.getDetails());
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);

            // Логируем успешное обновление
            System.out.println("✅ Контекст безопасности обновлен: " + oldLogin + " → " + updatedClient.getLogin());
        }
    }

    @Transactional
    public boolean updatePassword(Long clientId, String currentPassword, String newPassword) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        // Валидация пароля
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("Новый пароль должен содержать не менее 6 символов");
        }

        if (newPassword.length() > 100) {
            throw new RuntimeException("Пароль слишком длинный");
        }

        // Проверка текущего пароля
        if (!passwordEncoder.matches(currentPassword, client.getPassword())) {
            return false;
        }

        // Обновление пароля
        client.setPassword(passwordEncoder.encode(newPassword));
        clientRepository.save(client);
        return true;
    }

    @Transactional
    public void addOrUpdateCarInfo(Long clientId, ClientProfileDTO carDTO) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        // Валидация данных автомобиля
        validateCarData(carDTO);

        // Проверка уникальности VIN кода
        if (carDTO.getVinCode() != null && !carDTO.getVinCode().trim().isEmpty()) {
            String vinCode = carDTO.getVinCode().trim();
            if (carDetailRepository.existsByVinCode(vinCode)) {
                // Если VIN уже существует, проверяем принадлежит ли он текущему клиенту
                Optional<CarDetail> existingCarDetail = carDetailRepository.findByVinCode(vinCode);
                if (existingCarDetail.isPresent() &&
                        (client.getCar() == null || !existingCarDetail.get().getId().equals(client.getCar().getId()))) {
                    throw new RuntimeException("VIN код уже используется другим автомобилем");
                }
            }
        }

        Car car;
        CarDetail carDetail;

        if (client.getCar() != null) {
            // Обновляем существующий автомобиль
            car = client.getCar();
            car.setCarBrand(carDTO.getCarBrand());
            car.setCarModel(carDTO.getCarModel());
            car = carRepository.save(car);

            // Обновляем детали автомобиля
            carDetail = carDetailRepository.findById(car.getId())
                    .orElseThrow(() -> new RuntimeException("Детали автомобиля не найдены"));
            carDetail.setFuelType(carDTO.getFuelType());
            carDetail.setEngineCode(carDTO.getEngineCode());
            carDetail.setVinCode(carDTO.getVinCode());
            carDetail.setCar(car);
        } else {
            // Создаем новый автомобиль
            car = new Car(carDTO.getCarBrand(), carDTO.getCarModel());
            car = carRepository.save(car);

            // Создаем детали автомобиля
            carDetail = new CarDetail();
            carDetail.setFuelType(carDTO.getFuelType());
            carDetail.setEngineCode(carDTO.getEngineCode());
            carDetail.setVinCode(carDTO.getVinCode());
            carDetail.setCar(car);
        }

        carDetailRepository.save(carDetail);

        // Связываем клиента с автомобилем
        client.setCar(car);
        clientRepository.save(client);
    }

    public ClientProfileDTO getClientProfileDTO(Long clientId) {
        Client client = getClientProfile(clientId);
        ClientProfileDTO dto = new ClientProfileDTO();

        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setSurname(client.getSurname());
        dto.setPatronymic(client.getPatronymic());
        dto.setPhone(client.getPhone());
        dto.setEmail(client.getEmail());
        dto.setLogin(client.getLogin());

        // Заполняем данные об автомобиле если они есть
        if (client.getCar() != null) {
            Car car = client.getCar();
            dto.setCarBrand(car.getCarBrand());
            dto.setCarModel(car.getCarModel());

            // Ищем детали автомобиля
            Optional<CarDetail> carDetail = carDetailRepository.findById(car.getId());
            if (carDetail.isPresent()) {
                dto.setFuelType(carDetail.get().getFuelType());
                dto.setEngineCode(carDetail.get().getEngineCode());
                dto.setVinCode(carDetail.get().getVinCode());
            }
        }

        return dto;
    }

    public ClientProfileDTO getClientProfileDTOByLogin(String login) {
        Client client = clientRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
        return getClientProfileDTO(client.getId());
    }

    /**
     * Проверяет, изменился ли логин при обновлении профиля
     * Используется в контроллере для определения необходимости перелогина
     */
    public boolean isLoginChanged(Long clientId, String newLogin) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
        return !client.getLogin().equals(newLogin);
    }
}