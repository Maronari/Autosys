package mirea.edu.autosys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mirea.edu.autosys.model.Temperature;
import mirea.edu.autosys.repository.AutosysRepo;

@Service
public class DatabaseService {
    @Autowired
    private AutosysRepo repository;

    public List<Temperature> getAllEntities() {
        return repository.findAll();
    }

    public Temperature saveTemperature(Temperature entity) {
        return repository.save(entity);
    }
}
