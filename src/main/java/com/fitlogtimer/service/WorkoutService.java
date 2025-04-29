package com.fitlogtimer.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import static com.fitlogtimer.constants.ExerciseSetConstants.SetTypes.HEAVY;
import static com.fitlogtimer.constants.ExerciseSetConstants.SetTypes.LIGHT_50;
import static com.fitlogtimer.constants.ExerciseSetConstants.SetTypes.MEDIUM_55;

import com.fitlogtimer.constants.ExerciseColorConstants;
import com.fitlogtimer.constants.ExerciseSetType;
import com.fitlogtimer.constants.WorkoutColorConstants;
import com.fitlogtimer.dto.base.SetBasicDTO;
import com.fitlogtimer.dto.base.SetBasicElasticDTO;
import com.fitlogtimer.dto.base.SetBasicInterfaceDTO;
import com.fitlogtimer.dto.base.SetBasicIsometricDTO;
import com.fitlogtimer.dto.base.SetBasicMovementDTO;
import com.fitlogtimer.dto.create.ExerciseSetCreateDTO;
import com.fitlogtimer.dto.create.WorkoutCreateDTO;
import com.fitlogtimer.dto.details.LastSetDTO;
import com.fitlogtimer.dto.details.WorkoutDetailsBrutDTO;
import com.fitlogtimer.dto.details.WorkoutDetailsGroupedDTO;
import com.fitlogtimer.dto.display.CalendarDTO;
import com.fitlogtimer.dto.display.WorkoutListDisplayDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDCHeavyDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDCLightDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDCVarDTO;
import com.fitlogtimer.dto.fromxlsx.FromXlsxDeadliftDTO;
import com.fitlogtimer.dto.listitem.CalendarItemDTO;
import com.fitlogtimer.dto.listitem.SetGroupCleanWorkoutListItemDTO;
import com.fitlogtimer.dto.listitem.SetWorkoutListItemDTO;
import com.fitlogtimer.dto.listitem.WorkoutListItemDTO;
import com.fitlogtimer.dto.postgroup.SetsAllDifferentDTO;
import com.fitlogtimer.dto.postgroup.SetsAllDifferentElasticDTO;
import com.fitlogtimer.dto.postgroup.SetsAllDifferentIsometricDTO;
import com.fitlogtimer.dto.postgroup.SetsAllDifferentMovementDTO;
import com.fitlogtimer.dto.postgroup.SetsSameBandsAndRepsDTO;
import com.fitlogtimer.dto.postgroup.SetsSameBandsDTO;
import com.fitlogtimer.dto.postgroup.SetsSameDurationRepsAndWeightDTO;
import com.fitlogtimer.dto.postgroup.SetsSameMovementDTO;
import com.fitlogtimer.dto.postgroup.SetsSameRepsDTO;
import com.fitlogtimer.dto.postgroup.SetsSameRepsElasticDTO;
import com.fitlogtimer.dto.postgroup.SetsSameWeightAndRepsDTO;
import com.fitlogtimer.dto.postgroup.SetsSameWeightDTO;
import com.fitlogtimer.dto.transition.SetInWorkoutDTO;
import com.fitlogtimer.dto.transition.SetsGroupedDTO;
import com.fitlogtimer.dto.transition.SetsGroupedWithNameDTO;
import com.fitlogtimer.exception.NotFoundException;
import com.fitlogtimer.mapper.ExerciseSetMapper;
import com.fitlogtimer.mapper.WorkoutMapper;
import com.fitlogtimer.model.Exercise;
import com.fitlogtimer.model.ExerciseSet;
import com.fitlogtimer.model.Workout;
import com.fitlogtimer.model.sets.ElasticSet;
import com.fitlogtimer.model.sets.FreeWeightSet;
import com.fitlogtimer.model.sets.IsometricSet;
import com.fitlogtimer.model.sets.MovementSet;
import com.fitlogtimer.repository.ExerciseRepository;
import com.fitlogtimer.repository.ExerciseSetRepository;
import com.fitlogtimer.repository.WorkoutRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class WorkoutService {

    private final WorkoutRepository workoutRepository;

    private final WorkoutMapper workoutMapper;

    private final ExerciseSetMapper exerciseSetMapper;

    private final ExerciseRepository exerciseRepository;

    private final ExerciseSetService exerciseSetService;

    private final ExerciseSetRepository exerciseSetRepository;

    public List<WorkoutListDisplayDTO> getAllWorkoutsDisplayDTO() {
        List<Workout> workouts = workoutRepository.findAllByOrderByDateDesc();
        Map<Integer, List<String>> exercises = getExerciseNamesForWorkouts(workouts);

        return workouts.stream()
            .map(workout -> workoutMapper.toWorkoutDisplayDTO(
                workout,
                exercises.getOrDefault(workout.getId(), List.of())
        ))
            .toList();

    }

    public CalendarDTO getCalendar(){
        
        return new CalendarDTO(getCalendarItems(), ExerciseColorConstants.COLORS, WorkoutColorConstants.COLORS);
    }

    public List<CalendarItemDTO> getCalendarItems() {
        List<Workout> workouts = workoutRepository.findAll(); // ordre pas important ici, c'est JS qui trie par date
        Map<Integer, List<String>> exercises = getExerciseNamesForWorkouts(workouts);
    
        return workouts.stream()
                .map(workout -> new CalendarItemDTO(
                        workout.getId(),
                        workout.getType(),
                        workout.getDate(),
                        exercises.get(workout.getId()))
                )
                .toList();
        }

    public List<WorkoutListItemDTO> getAllWorkoutsDTO() {
        List<Workout> workouts = workoutRepository.findAllByOrderByDateDesc();
        Map<Integer, List<String>> exercises = getExerciseNamesForWorkouts(workouts);

        return workouts.stream()
                .map(workout -> workoutMapper.toWorkoutListItemDTO(
                    workout,
                    exercises.getOrDefault(workout.getId(), List.of())))
                .toList();
    }
    
    public List<Workout> getAllWorkouts() {
        return workoutRepository.findAllByOrderByDateDesc();
    }
    
    @Transactional
    public WorkoutListItemDTO createWorkout(WorkoutCreateDTO workoutInDTO) {
        
        Workout savedWorkout = workoutRepository.save(workoutMapper.toEntity(workoutInDTO));
        
        return workoutMapper.toWorkoutListItemDTO(savedWorkout, getExerciseShortNamesByWorkoutId(savedWorkout.getId()));
    }

    @Transactional
    public boolean deleteWorkout(int workoutId) {
        if (workoutRepository.existsById(workoutId)) {
            workoutRepository.deleteById(workoutId);
            return true;
        }
        return false;
    }

    public WorkoutDetailsBrutDTO getWorkoutDetailsBrut(int id){
        Workout workout = workoutRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Workout not found: " + id));

        List<SetWorkoutListItemDTO> setsDTO = getSetsOutDTO(workout);
        log.info(setsDTO.toString());
        return new WorkoutDetailsBrutDTO(id, workout.getDate(), workout.getBodyWeight(), workout.getComment(), setsDTO);
    }
    
    public WorkoutDetailsGroupedDTO getWorkoutGrouped(int id){
        Workout workout = workoutRepository.findById(id)
            .orElseThrow(()->new NotFoundException("Workout not found: " + id));

        List<SetsGroupedDTO> groupedSets = groupConsecutiveSetsByExercise(getSetsDTO(workout));

        List<SetsGroupedWithNameDTO> groupedSetsWithName = groupedSets.stream()
                .map(this::groupedSetToGroupedSetWithName)
                .toList();

        List<SetGroupCleanWorkoutListItemDTO> finalGroupedSets = groupedSetsWithName.stream()
                .map(this::cleanSetsGroup)
                .toList();
        
        WorkoutDetailsGroupedDTO workoutGroupedDTO = new WorkoutDetailsGroupedDTO(id, workout.getDate(), workout.getBodyWeight(), workout.getComment(), finalGroupedSets);
        System.out.println(workoutGroupedDTO);
        return workoutGroupedDTO;
    }
    
    public List<SetsGroupedDTO> groupConsecutiveSetsByExercise(List<SetInWorkoutDTO> exerciseSets){
        List<SetsGroupedDTO> groupedSets = new ArrayList<>();
        List<SetInWorkoutDTO> currentGroup = new ArrayList<>();

        for(int i=0; i < exerciseSets.size(); i++){
            SetInWorkoutDTO currentSet = exerciseSets.get(i);

            if (i==0 || currentSet.exercise_id() == exerciseSets.get(i-1).exercise_id()) {
                currentGroup.add(currentSet);
            } else {
                groupedSets.add(new SetsGroupedDTO(new ArrayList<>(currentGroup)));
                currentGroup.clear();
                currentGroup.add(currentSet);
            }
            log.debug("{} after: groupedSets: {}, current: {}", i, groupedSets, currentGroup);
        }

        if(!currentGroup.isEmpty()){
            groupedSets.add(new SetsGroupedDTO(currentGroup));
        }

        return groupedSets;
    }

    public SetsGroupedWithNameDTO groupedSetToGroupedSetWithName(SetsGroupedDTO entrySet){
        int exerciseId = entrySet.setGroup().get(0).exercise_id();
        Exercise exercise = exerciseRepository.findById(exerciseId)
                            .orElseThrow(()->new NoSuchElementException("Exercise not found with id: " + exerciseId));
        log.info("*-* Exercise: {}", exercise);
        String shortName = exercise.getShortName();
        String exerciseType = exercise.getType();

        List<SetBasicInterfaceDTO> sets = List.of();
        
        if(exerciseType.equals(ExerciseSetType.FREE_WEIGHT) || exerciseType.equals(ExerciseSetType.BODYWEIGHT)){
            //log.info("*-*-* Creating SetBasic");
            sets = entrySet.setGroup().stream()
                    .map(set -> (SetBasicInterfaceDTO) new SetBasicDTO(set.repNumber(), set.weight()))
                    .toList();
        } else if (exerciseType.equals(ExerciseSetType.ELASTIC)){
            //log.info("*-*-* Creating SetBasicElastic");
            sets = entrySet.setGroup().stream()
                    .map(set -> (SetBasicInterfaceDTO) new SetBasicElasticDTO(set.repNumber(), set.bands()))
                    .toList();
        } else if (exerciseType.equals(ExerciseSetType.ISOMETRIC)){
            //log.info("*-*-* Creating SetBasicIsometric");
            sets = entrySet.setGroup().stream()
                    .map(set -> (SetBasicInterfaceDTO) new SetBasicIsometricDTO(set.durationS(), set.repNumber(), set.weight()))
                    .toList();
        } else if (exerciseType.equals(ExerciseSetType.MOVEMENT)){
            //log.info("*-*-* Creating SetBasicMovement");
            sets = entrySet.setGroup().stream()
                    .map(set -> (SetBasicInterfaceDTO) new SetBasicMovementDTO(set.repNumber(), set.distance(), set.bands(), set.weight()))
                    .toList();
        }

        return new SetsGroupedWithNameDTO(shortName, sets);        
    }

    public List<SetInWorkoutDTO> getSetsDTO(Workout workout){
        log.debug("*** getSetDTO ***");
        
        return workout.getSetRecords().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private SetInWorkoutDTO mapToDTO(ExerciseSet exerciseSet) {
        //if (set instanceof FreeWeightSet freeSet) {
            log.debug("***: {}",exerciseSet.toString());
            return exerciseSetMapper.toSetInWorkoutDTO(/*freeS*/exerciseSet);
       // }
    }

    public List<SetWorkoutListItemDTO> getSetsOutDTO(Workout workout){
        log.error("{}", workout.getSetRecords());
        return workout.getSetRecords().stream()
                .map(exerciseSetMapper::toSetListItemDTO)
                .collect(Collectors.toList());
    }

    public SetGroupCleanWorkoutListItemDTO cleanSetsGroup(SetsGroupedWithNameDTO sets) {
        SetBasicInterfaceDTO firstSet = sets.sets().get(0);

        if (firstSet instanceof SetBasicDTO) {
            //log.info("*-*-* : SetBasicDTO");
            return cleanSetsGroupForSetBasic(sets);
        } else if (firstSet instanceof SetBasicElasticDTO) {
            //log.info("*-*-* : SetBasicElasticDTO");
            return cleanSetsGroupForSetBasicElastic(sets);
        } else if (firstSet instanceof SetBasicIsometricDTO) {
            //log.info("*-*-* : SetBasicIsometricDTO");
            return cleanSetsGroupForSetBasicIsometric(sets);
        } else if (firstSet instanceof SetBasicMovementDTO) {
            //log.info("*-*-* : SetBasicMovementDTO");
            return cleanSetsGroupForSetBasicMovement(sets);
        }

        else 
        {
            throw new IllegalArgumentException("Unsupported set type: " + firstSet.getClass());
        }
    }
    
    public SetGroupCleanWorkoutListItemDTO cleanSetsGroupForSetBasicIsometric(SetsGroupedWithNameDTO sets) {
        // ISOMETRIC
    
        List<SetBasicIsometricDTO> classicSets = sets.sets().stream()
            .map(set -> (SetBasicIsometricDTO) set)
            .toList();
    
        if (hasSameDuration(sets)) {
            if (hasSameReps(sets)) {
                if (hasSameWeight(sets)){
                    return new SetGroupCleanWorkoutListItemDTO(
                    sets.exerciseNameShort(),
                    new SetsSameDurationRepsAndWeightDTO(
                        classicSets.size(),
                        classicSets.get(0).durationS(),
                        classicSets.get(0).repNumber(),
                        classicSets.get(0).weight()
                    )
                );}       
            } 
        }  
        
        return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsAllDifferentIsometricDTO(classicSets)
            );
        
    }

    public SetGroupCleanWorkoutListItemDTO cleanSetsGroupForSetBasicMovement(SetsGroupedWithNameDTO sets) {
        // MOVEMENT
    
        List<SetBasicMovementDTO> classicSets = sets.sets().stream()
            .map(set -> (SetBasicMovementDTO) set)
            .toList();
    
        if (hasSameMovementSets(sets)) {
            return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsSameMovementDTO(
                    classicSets.size(),
                    classicSets.get(0).repNumber(),
                    classicSets.get(0).distance(),
                    classicSets.get(0).bands(),
                    classicSets.get(0).weight()
                ));
            }  
   
        
        return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsAllDifferentMovementDTO(classicSets)
            );
        
    }

    public SetGroupCleanWorkoutListItemDTO cleanSetsGroupForSetBasicElastic(SetsGroupedWithNameDTO sets) {
        // ELASTIC
    
        List<SetBasicElasticDTO> classicSets = sets.sets().stream()
            .map(set -> (SetBasicElasticDTO) set)
            .toList();
    
        if (hasSameBands(sets)) {
            if (hasSameReps(sets)) {
                return new SetGroupCleanWorkoutListItemDTO(
                    sets.exerciseNameShort(),
                    new SetsSameBandsAndRepsDTO(
                        classicSets.size(),
                        classicSets.get(0).repNumber(),
                        classicSets.get(0).bands()
                    )
                );
            } else {
                List<Integer> repsSet = classicSets.stream()
                    .map(SetBasicElasticDTO::repNumber)
                    .toList();
    
                return new SetGroupCleanWorkoutListItemDTO(
                    sets.exerciseNameShort(),
                    new SetsSameBandsDTO(
                        classicSets.get(0).bands(),
                        repsSet
                    )
                );
            }
        } else if (hasSameReps(sets)) {
            List<String> bandsList = classicSets.stream()
                .map(SetBasicElasticDTO::bands)
                .toList();
    
            return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsSameRepsElasticDTO(
                    classicSets.get(0).repNumber(),
                    bandsList
                )
            );
        } else {
            return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsAllDifferentElasticDTO(classicSets)
            );
        }
    }
    
    public SetGroupCleanWorkoutListItemDTO cleanSetsGroupForSetBasic(SetsGroupedWithNameDTO sets) {
        // FREE_WEIGHT ou BODYWEIGHT
    
        List<SetBasicDTO> classicSets = sets.sets().stream()
            .map(set -> (SetBasicDTO) set)
            .toList();
    
        if (hasSameWeight(sets)) {
            if (hasSameReps(sets)) {
                return new SetGroupCleanWorkoutListItemDTO(
                    sets.exerciseNameShort(),
                    new SetsSameWeightAndRepsDTO(
                        classicSets.size(),
                        classicSets.get(0).repNumber(),
                        classicSets.get(0).weight()
                    )
                );
            } else {
                List<Integer> repsSet = classicSets.stream()
                    .map(SetBasicDTO::repNumber)
                    .toList();
    
                return new SetGroupCleanWorkoutListItemDTO(
                    sets.exerciseNameShort(),
                    new SetsSameWeightDTO(
                        classicSets.get(0).weight(),
                        repsSet
                    )
                );
            }
        } else if (hasSameReps(sets)) {
            List<Double> weights = classicSets.stream()
                .map(SetBasicDTO::weight)
                .toList();
    
            return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsSameRepsDTO(
                    classicSets.get(0).repNumber(),
                    weights
                )
            );
        } else {
            return new SetGroupCleanWorkoutListItemDTO(
                sets.exerciseNameShort(),
                new SetsAllDifferentDTO(classicSets)
            );
        }
    }
    
    public boolean hasSameWeight(SetsGroupedWithNameDTO sets) {
        if (sets.sets().isEmpty()) return true;

        SetBasicInterfaceDTO firstSet = sets.sets().get(0);
        
        if (firstSet instanceof SetBasicDTO){
            double firstWeight = ((SetBasicDTO) firstSet).weight();
            for (SetBasicInterfaceDTO set : sets.sets()) {
                double currentWeight = ((SetBasicDTO) set).weight();
                if (currentWeight != firstWeight) {
                    return false;
                }
            }}
        
        else if (firstSet instanceof SetBasicIsometricDTO){
            double firstWeight = ((SetBasicIsometricDTO) firstSet).weight();

            for (SetBasicInterfaceDTO set : sets.sets()) {
                double currentWeight = ((SetBasicIsometricDTO) set).weight();
                if (currentWeight != firstWeight) {
                    return false;
                }
            }}

        return true;
    }

    public boolean hasSameBands(SetsGroupedWithNameDTO sets) {
        if (sets.sets().isEmpty()) return true;

        String firstBands = ((SetBasicElasticDTO) sets.sets().get(0)).bands();

        for (SetBasicInterfaceDTO set : sets.sets()) {
            String currentBands = ((SetBasicElasticDTO) set).bands();
            if (currentBands != firstBands) {
                return false;
            }
        }

        return true;
    }

    public boolean hasSameDuration(SetsGroupedWithNameDTO sets) {
        if (sets.sets().isEmpty()) return true;

        int firstDuration = ((SetBasicIsometricDTO) sets.sets().get(0)).durationS();

        for (SetBasicInterfaceDTO set : sets.sets()) {
            int currentDuration = ((SetBasicIsometricDTO) set).durationS();
            if (currentDuration != firstDuration) {
                return false;
            }
        }

        return true;
    }

    public boolean hasSameMovementSets(SetsGroupedWithNameDTO sets){
        if (sets.sets().isEmpty()) return true;

        SetBasicInterfaceDTO firstSet = sets.sets().get(0);

        double firstReps = ((SetBasicMovementDTO) firstSet).repNumber();
        String firstDistance = ((SetBasicMovementDTO) firstSet).distance();
        String firstBands = ((SetBasicMovementDTO) firstSet).bands();
        double firstWeight = ((SetBasicMovementDTO) firstSet).weight();

        for (SetBasicInterfaceDTO set : sets.sets()) {
            int currentReps = ((SetBasicMovementDTO) set).repNumber();
            String currentDistance = ((SetBasicMovementDTO) set).distance();
            String currentBands = ((SetBasicMovementDTO) set).bands();
            double currentWeight = ((SetBasicMovementDTO) set).weight();
            if (currentReps!=firstReps || currentDistance!=firstDistance || currentBands!=firstBands || currentWeight!=firstWeight) {
                return false;
            }
        }        

        return true;
    }

    public boolean hasSameReps(SetsGroupedWithNameDTO sets) {
        if (sets.sets().isEmpty()) return true;
        
        SetBasicInterfaceDTO firstSet = sets.sets().get(0);
        
        if (firstSet instanceof SetBasicDTO){
            double firstReps = ((SetBasicDTO) firstSet).repNumber();

            for (SetBasicInterfaceDTO set : sets.sets()) {
                double currentReps = ((SetBasicDTO) set).repNumber();
                if (currentReps != firstReps) {
                    return false;
                }
            }}
        else if (firstSet instanceof SetBasicElasticDTO){
            double firstReps = ((SetBasicElasticDTO) firstSet).repNumber();

            for (SetBasicInterfaceDTO set : sets.sets()) {
                double currentReps = ((SetBasicElasticDTO) set).repNumber();
                if (currentReps != firstReps) {
                    return false;
                }
            }}
        
        else if (firstSet instanceof SetBasicIsometricDTO){
            double firstReps = ((SetBasicIsometricDTO) firstSet).repNumber();

            for (SetBasicInterfaceDTO set : sets.sets()) {
                double currentReps = ((SetBasicIsometricDTO) set).repNumber();
                if (currentReps != firstReps) {
                    return false;
                }
            }}
        return true;
    }

    public LastSetDTO getLastSetDTO(int id){

        Optional<Workout> optionalWorkout = workoutRepository.findById(id);
        if (optionalWorkout.isEmpty()) {return null;}
    
        Workout workout = optionalWorkout.get();
    
        List<ExerciseSet> sets = workout.getSetRecords();
        if (sets == null || sets.isEmpty()) {return null;}
    
        ExerciseSet lastSet = sets.get(sets.size() - 1);
    
        if (lastSet == null || lastSet.getExercise() == null) {return null;}
    
        return exerciseSetMapper.toLastSetDTO(lastSet);
    }

    public ExerciseSetCreateDTO setFormByLastSetDTO(int id) {

        int defaultExerciseId = id;
        double defaultWeight = 0.0;
        int defaultRepNumber = 0;
        String defaultBands = "-";
        int defaultDuration = 0;
        String defaultDistance = "";
        String defaultTag = "";
        String defaultType = ExerciseSetType.FREE_WEIGHT;
        String defaultComment = "";
    
        Optional<Workout> optionalWorkout = workoutRepository.findById(id);
        if (optionalWorkout.isEmpty()) {
            return new ExerciseSetCreateDTO(defaultExerciseId, defaultWeight, defaultRepNumber, defaultBands, defaultDuration, defaultDistance, defaultTag,defaultComment, id, defaultType);
        }
    
        Workout workout = optionalWorkout.get();
    
        List<ExerciseSet> sets = workout.getSetRecords();
        if (sets == null || sets.isEmpty()) {
            return new ExerciseSetCreateDTO(defaultExerciseId, defaultWeight, defaultRepNumber, defaultBands, defaultDuration, defaultDistance, defaultTag,defaultComment, id, defaultType);
        }
    
        ExerciseSet lastSet = sets.get(sets.size() - 1);
    
        if (lastSet == null || lastSet.getExercise() == null) {
            return new ExerciseSetCreateDTO(defaultExerciseId, defaultWeight, defaultRepNumber, defaultBands, defaultDuration, defaultDistance, defaultTag,defaultComment, id, defaultType);
        }
    
        if (lastSet instanceof FreeWeightSet freeWeightSet) {
            defaultWeight = freeWeightSet.getWeight();
        }

        if (lastSet instanceof ElasticSet elasticSet){
            defaultBands = elasticSet.getBands();
        }

        if (lastSet instanceof IsometricSet isometricSet){
            defaultDuration = isometricSet.getDurationS();
            defaultWeight = isometricSet.getWeight();
        }

        if (lastSet instanceof MovementSet movementSet){
            defaultBands = movementSet.getBands();
            defaultDistance = movementSet.getDistance();
            defaultWeight = movementSet.getWeight();
        }

        return new ExerciseSetCreateDTO(
            lastSet.getExercise().getId(),
            defaultWeight,
            lastSet.getRepNumber(),
            defaultBands,
            defaultDuration,
            defaultDistance,
            defaultTag,
            defaultComment,
            id,
            defaultType
        );
    }

    public List<String> getExerciseShortNamesByWorkoutId(int id){
        return workoutRepository.findExerciseShortNamesByWorkoutId(id);
    }

    private Map<Integer, List<String>> getExerciseNamesForWorkouts(List<Workout> workouts) {
        List<ExerciseSet> sets = exerciseSetRepository.findByWorkoutIdIn(
            workouts.stream().map(Workout::getId).toList()
        );
        return sets.stream()
            .collect(Collectors.groupingBy(
                exerciseSet -> exerciseSet.getWorkout().getId(),
                Collectors.mapping(
                    exerciseSet -> exerciseSet.getExercise().getShortName(),
                    Collectors.toCollection(LinkedHashSet::new) //garde l'ordre en supprimant les doublons
                )
            ))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new ArrayList<>(entry.getValue())
            ));
    }

    @Transactional
    public void addExerciseSet(ExerciseSetCreateDTO exerciseSetInDTO){
        exerciseSetService.saveExerciseSet(exerciseSetInDTO);
    }

    @Transactional
    public void deleteByTagImport(String tagImport){
        workoutRepository.deleteByTagImport(tagImport);
    }







    /*
     * CREATE FROM XLS
     */
    
    //  @Transactional
    //  public List<Workout> createWorkoutsFromXlsxDCHeavyDTOList(List<FromXlsxDCHeavyDTO> dtoList) {
    //      log.info("*** Import from Heavy Sheet ***");
    //      return dtoList.stream()
    //          .map(this::createWorkoutFromXlsxDCHeavyDTO)
    //          .collect(Collectors.toList());
    //  }
 
    //  @Transactional
    //  public List<Workout> createWorkoutsFromXlsxDCLightDTOList(List<FromXlsxDCLightDTO> dtoList) {
    //      log.info("*** Import from Light Sheet ***");
    //      return dtoList.stream()
    //          .map(this::createWorkoutFromXlsxDCLightDTO)
    //          .collect(Collectors.toList());
    //  }
 
    //  @Transactional
    //  public List<Workout> createWorkoutsFromXlsxDCVarDTOList(List<FromXlsxDCVarDTO> dtoList) {
    //      log.info("*** Import from Var Sheet ***");
    //      return dtoList.stream()
    //          .map(this::createWorkoutFromXlsxDCVarDTO)
    //          .collect(Collectors.toList());
    //  }
 
    //  @Transactional
    //  public List<Workout> createWorkoutsFromXlsxDeadliftDTOList(List<FromXlsxDeadliftDTO> dtoList) {
    //      log.info("*** Import from Deadlift Sheet ***");
    //      return dtoList.stream()
    //          .map(this::createWorkoutFromXlsxDeadliftDTO)
    //          .collect(Collectors.toList());
    //  }
     
    //  @Transactional
    //  public Workout createWorkoutFromXlsxDCHeavyDTO(FromXlsxDCHeavyDTO fromXlsxDCHeavyDTO){
    //      Workout workout = workoutMapper.toEntity(fromXlsxDCHeavyDTO);
    //      int idWorkout = workoutRepository.save(workout).getId();
    //      fromXlsxDCHeavyDTO.sets().stream()
    //          .map(basicSet -> {
    //                  int position = fromXlsxDCHeavyDTO.sets().indexOf(basicSet) + 1; // 1-based index
    //                  String tag;
                     
    //                  if (position <= 3) {
    //                      tag = HEAVY;
    //                  } else if (position == 5) {
    //                      tag = MEDIUM_55;
    //                  } else if (position == 6) {
    //                      tag = LIGHT_50;
    //                  } else if (position == 4) {
    //                      tag = basicSet.weight() > 56 ? HEAVY : MEDIUM_55;
    //                  } else {
    //                      tag = "";
    //                  }
         
    //              return new ExerciseSetCreateDTO(
    //                  exerciseRepository.findByShortName("DC").getId(),
    //                  basicSet.weight(),
    //                  basicSet.repNumber(),
    //                  tag,
    //                  "",
    //                  idWorkout,
    //                  ExerciseSetType.FREE_WEIGHT
    //              );
    //              })
    //          .forEach(exerciseSetInDTO -> {
    //              addExerciseSet(exerciseSetInDTO);
    //          });
 
    //          log.info("Workout saved: {}", workoutRepository.getReferenceById(idWorkout));
    //      return workout;
    //  }
 
    //  @Transactional
    //  public Workout createWorkoutFromXlsxDCLightDTO(FromXlsxDCLightDTO fromXlsxDCLightDTO){
    //      Workout workout = workoutMapper.toEntity(fromXlsxDCLightDTO);
    //      int idWorkout = workoutRepository.save(workout).getId();
    //      //log.info("*** workout {} created", idWorkout);
    //      fromXlsxDCLightDTO.sets().stream()
    //          .map(basicSet -> {
    //              //log.info("**/*//** {}, short: ", basicSet, basicSet.exercise());
    //              return new ExerciseSetCreateDTO(
    //                  exerciseRepository.findByShortName(basicSet.exercise()).getId(),
    //                  basicSet.weight(),
    //                  basicSet.repNumber(),
    //                  "",
    //                  "",
    //                  idWorkout,
    //                  ExerciseSetType.FREE_WEIGHT
    //              );
    //              })
    //          .forEach(exerciseSetInDTO -> {
    //              //log.info("-*-*-*-*-*-*-*-*-*-*{}", exerciseSetInDTO);
    //              addExerciseSet(exerciseSetInDTO);
    //          });
 
    //      log.info("Workout saved: {}", workoutRepository.getReferenceById(idWorkout));
    //      return workout;
    //  }
 
    //  @Transactional
    //  public Workout createWorkoutFromXlsxDCVarDTO(FromXlsxDCVarDTO fromXlsxDCVarDTO){
    //      Workout workout = workoutMapper.toEntity(fromXlsxDCVarDTO);
         
    //      final int idWorkout;
    //      if(!workoutRepository.existsByDateAndTagImport(fromXlsxDCVarDTO.date(), "importH")){
    //          idWorkout = workoutRepository.save(workout).getId();
    //      } else {
    //          idWorkout = workoutRepository.findByDateAndTagImport(fromXlsxDCVarDTO.date(), "importH").getId();
    //          log.info("Using existing workout: {}", idWorkout);
    //      }
         
    //      //log.info("*** workout {} created", idWorkout);
    //      fromXlsxDCVarDTO.sets().stream()
    //          .map(basicSet -> {
    //              //log.info("**/*//** {}, short: ", basicSet, basicSet.exercise());
    //              return new ExerciseSetCreateDTO(
    //                  exerciseRepository.findByShortName(basicSet.exercise()).getId(),
    //                  basicSet.weight(),
    //                  basicSet.repNumber(),
    //                  "",
    //                  "",
    //                  idWorkout,
    //                  ExerciseSetType.FREE_WEIGHT
    //              );
    //              })
    //          .forEach(exerciseSetInDTO -> {
    //              //log.info("-*-*-*-*-*-*-*-*-*-*{}", exerciseSetInDTO);
    //              addExerciseSet(exerciseSetInDTO);
    //          });
 
    //      log.info("Workout saved: {}", workoutRepository.getReferenceById(idWorkout));
    //      return workout;
    //  }
 
    //  @Transactional
    //  public Workout createWorkoutFromXlsxDeadliftDTO(FromXlsxDeadliftDTO fromXlsxDeadliftDTO){
    //      Workout workout = workoutMapper.toEntity(fromXlsxDeadliftDTO);
    //      //int idWorkout = workoutRepository.save(workout).getId();
    //      final int idWorkout;
    //      if(!workoutRepository.existsByDateAndTagImport(fromXlsxDeadliftDTO.date(), "importL")){
    //          idWorkout = workoutRepository.save(workout).getId();
    //          log.info("New workout created: {}", idWorkout);
    //      } else {
    //          idWorkout = workoutRepository.findByDateAndTagImport(fromXlsxDeadliftDTO.date(), "importL").getId();
    //          log.info("Using existing workout: {}", idWorkout);
    //      }
        
    //      //un seul elément ds le format récupéré
    //      addExerciseSet(new ExerciseSetCreateDTO(
    //                  exerciseRepository.findByShortName("DL").getId(),
    //                  fromXlsxDeadliftDTO.sets().weight(),
    //                  fromXlsxDeadliftDTO.sets().repNumber(),
    //                  "ONE DL",
    //                  "",
    //                  idWorkout,
    //                  ExerciseSetType.FREE_WEIGHT
    //      ));
 
    //      log.info("Workout saved: {}", workoutRepository.getReferenceById(idWorkout));
    //      return workout;
    //  }

}
