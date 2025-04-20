package mirea.edu.autosys.utils;

import org.springframework.stereotype.Service;

@Service
public class PidController {
    private double kP = 0.5; // Пропорциональный коэффициент
    private double kI = 0.1; // Интегральный коэффициент
    private double kD = 0.2; // Дифференциальный коэффициент
    private double integral = 0.0;
    private double previousError = 0.0;

    public double calculate(double setpoint, double actual, double dt) {
        double error = setpoint - actual;
        integral += error * dt;
        double derivative = (error - previousError) / dt;
        previousError = error;

        return kP * error + kI * integral + kD * derivative;
    }

    // Методы для настройки коэффициентов
    public void setTuningParameters(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }
}
