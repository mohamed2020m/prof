package com.leeuw.services;

import com.leeuw.doa.IDao;
import com.leeuw.entities.Specialite;
import com.leeuw.repositories.SpecialiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialiteService implements IDao<Specialite> {

    @Autowired
    private SpecialiteRepository specialiteRepository;

    @Override
    public Specialite create(Specialite o) {
        return specialiteRepository.save(o);
    }

    @Override
    public boolean delete(Specialite o) {
        try {
            specialiteRepository.delete(o);
            return true;
        }
        catch(Exception ex) {
            return false;
        }
    }

    @Override
    public Specialite update(Specialite o) {
        return specialiteRepository.save(o);
    }

    @Override
    public List<Specialite> findAll() {
        return specialiteRepository.findAll();
    }

    @Override
    public Specialite findById(Long id) {
        return specialiteRepository.findById(id).orElse(null);
    }

}
