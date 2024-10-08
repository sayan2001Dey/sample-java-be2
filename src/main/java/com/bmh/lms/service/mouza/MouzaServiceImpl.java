package com.bmh.lms.service.mouza;

import com.bmh.lms.dto.mouza.MouzaDTO;
import com.bmh.lms.model.Mouza;
import com.bmh.lms.model.MouzaCollection;
import com.bmh.lms.repository.MouzaCollectionRepository;
import com.bmh.lms.repository.MouzaRepository;
import com.bmh.lms.service.utils.CommonUtils;
import jakarta.transaction.Transactional;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MouzaServiceImpl implements MouzaService {

    @Autowired
    private MouzaRepository mouzaRepository;

    @Autowired
    private MouzaCollectionRepository mouzaMongoRepository;

    @Autowired
    private CommonUtils commonUtils;

    @Override
    @Transactional
    public MouzaDTO createMouza(MouzaDTO mouzaDTO, String username) {
        LocalDateTime ldt = LocalDateTime.now();

        Pair<Mouza, MouzaCollection> precessedData =  mouzaDTOToData(mouzaDTO);

        MouzaCollection mouzaCollection = precessedData.b;
        Mouza mouza = precessedData.a;

        if(mouzaCollection == null){
            throw new IllegalArgumentException("MouzaCollection cannot be null");
        }

        mouzaCollection = mouzaMongoRepository.save(mouzaCollection);

        mouza.setLandSpecifics(mouzaMongoRepository.save(mouzaCollection).getId());

        mouza.setMouzaId(commonUtils.generateUID("Mouza", "MZM"));
        mouza.setModified_type("INSERTED");
        mouza.setInserted_by(username);
        mouza.setInserted_on(ldt);
        mouza.setUpdated_by("NA");
        mouza.setUpdated_on(null);
        mouza.setDeleted_by("NA");
        mouza.setDeleted_on(null);

        mouza = mouzaRepository.save(mouza);

        MouzaDTO resultDTO;
        resultDTO = dataToMouzaDTO(mouza, mouzaCollection);

        return resultDTO;
    }

    @Override
    public List<MouzaDTO> getAllMouza() {
        List<Mouza> mouzaList =  mouzaRepository.findAllActiveByMouzaId();
        List<MouzaDTO> res = new ArrayList<>();

        mouzaList.forEach((mouza)->{
            MouzaCollection mouza2 = null;
            if(mouza.getLandSpecifics() != null)
                mouza2 = mouzaMongoRepository.findById(mouza.getLandSpecifics()).orElse(null);
            res.add(dataToMouzaDTO(mouza, mouza2));
        });

        return res;
    }

    @Override
    public MouzaDTO getMouzaById(String mouza_id) {
        Mouza mouza = mouzaRepository.findByMouzaId(mouza_id).orElse(null);
        if(mouza == null)
            return null;
        MouzaCollection mouza2 = mouzaMongoRepository.findById(mouza.getLandSpecifics()).orElse(null);
        if(mouza2 == null)
            return null;
        return dataToMouzaDTO(mouza, mouza2);
    }

    @Override
    @Transactional
    public MouzaDTO updateMouza(String mouza_id, MouzaDTO updatedMouzaDTO, String username) {
        LocalDateTime ldt = LocalDateTime.now();

        Mouza oldMouza = mouzaRepository.findByMouzaId(mouza_id).orElse(null);

        if(oldMouza == null) return null;

        Pair<Mouza, MouzaCollection> precessedData =  mouzaDTOToData(updatedMouzaDTO);

        MouzaCollection mouzaCollection = mouzaMongoRepository.save(precessedData.b);
        Mouza updatedMouza = precessedData.a;

        updatedMouza.setMouzaId(mouza_id);
        updatedMouza.setModified_type("INSERTED");
        updatedMouza.setInserted_by(oldMouza.getInserted_by());
        updatedMouza.setInserted_on(oldMouza.getInserted_on());
        updatedMouza.setUpdated_by(username);
        updatedMouza.setUpdated_on(ldt);
        updatedMouza.setDeleted_by("NA");
        updatedMouza.setDeleted_on(null);
        updatedMouza.setLandSpecifics(mouzaCollection.getId());

        oldMouza.setModified_type("UPDATED");
        oldMouza.setUpdated_by(username);
        oldMouza.setUpdated_on(ldt);

        mouzaRepository.save(oldMouza);
        return dataToMouzaDTO(mouzaRepository.save(updatedMouza), mouzaCollection);
    }

    @Override
    public Boolean deleteMouza(String mouza_id, String username) {
        LocalDateTime ldt = LocalDateTime.now();
        Mouza mouza = mouzaRepository.findByMouzaId(mouza_id).orElse(null);

        if(mouza == null) return false;

        mouza.setModified_type("DELETED");
        mouza.setDeleted_by(username);
        mouza.setDeleted_on(ldt);

        mouzaRepository.save(mouza);
        return true;
    }

    private Pair<Mouza, MouzaCollection> mouzaDTOToData(MouzaDTO mouzaDTO) {
        Mouza mouza = new Mouza();
        MouzaCollection mouzaCollection = new MouzaCollection();
        Pair<Mouza, MouzaCollection> res = new Pair<Mouza, MouzaCollection>(mouza, mouzaCollection);

        mouza.setMouza(mouzaDTO.getMouza());
        mouza.setGroupId(mouzaDTO.getGroupId());
        mouza.setBlock(mouzaDTO.getBlock());
        mouza.setJlno(mouzaDTO.getJlno());

        if(mouzaDTO.getLandSpecifics() == null)
            mouzaDTO.setLandSpecifics(new ArrayList<>());
        mouzaCollection.setLandSpecifics(mouzaDTO.getLandSpecifics());

        return res;
    }

    private MouzaDTO dataToMouzaDTO(Mouza mouza, MouzaCollection mouzaCollection) {
        if(mouza == null)
            return null;

        MouzaDTO res = new MouzaDTO();

        res.setMouzaId(mouza.getMouzaId());
        res.setGroupId(mouza.getGroupId());
        res.setMouza(mouza.getMouza());
        res.setBlock(mouza.getBlock());
        res.setJlno(mouza.getJlno());

        if(mouzaCollection == null || mouzaCollection.getLandSpecifics() == null) {
            res.setLandSpecifics(new ArrayList<>());
        } else
            res.setLandSpecifics(mouzaCollection.getLandSpecifics());

        return res;
    }
}
