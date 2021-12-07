package me.aleiv.core.paper.objects;

import java.util.List;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Note.Tone;

public class NoteBlockData {
    int octave;
    Tone tone;
    Instrument instrument;

    public static List<Tone> tones = List.of(Note.Tone.A, Note.Tone.B, Note.Tone.C, Note.Tone.D, Note.Tone.E, Note.Tone.F, Note.Tone.G);

    public NoteBlockData(Integer octave, Integer tone, Instrument instrument){
        this.octave = octave;
        this.tone = tones.get(tone);
        this.instrument = instrument;
    }

    public Note getNote(){
        return Note.natural(octave, tone);
    }
    
    public Instrument getInstrument(){
        return instrument;
    }
}
