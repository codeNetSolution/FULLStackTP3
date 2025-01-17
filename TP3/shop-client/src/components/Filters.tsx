import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    FormControl,
    InputLabel,
    MenuItem,
    Select,
    TextField,
} from '@mui/material';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DesktopDatePicker } from '@mui/x-date-pickers/DesktopDatePicker';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { Dayjs } from 'dayjs';
import { Dispatch, SetStateAction, useEffect, useState } from 'react';

type FiltersType = {
    inVacations: string;
    createdAfter: Dayjs | null;
    createdBefore: Dayjs | null;
    minProducts?: number | null;
    distinctCategories: string;
};

const transformFiltersToURL = (filters: FiltersType): string => {
    const transform = {
        ...filters,
        createdAfter: filters.createdAfter?.format('YYYY-MM-DD'),
        createdBefore: filters.createdBefore?.format('YYYY-MM-DD'),
    };

    let url = '';
    for (const [key, value] of Object.entries(transform)) {
        if (value) {
            url += `&${key}=${value}`;
        }
    }

    return url;
};

type Props = {
    setUrlFilters: Dispatch<SetStateAction<string>>;
    setSort: Dispatch<SetStateAction<string>>;
    sort: string;
};

const Filters = ({ setUrlFilters, setSort, sort }: Props) => {
    const defaultFilters: FiltersType = {
        inVacations: '',
        createdAfter: null,
        createdBefore: null,
        minProducts:null,
        distinctCategories: '',
    };
    const [open, setOpen] = useState<boolean>(false);
    const [filters, setFilters] = useState<FiltersType>(defaultFilters);

    useEffect(() => {
        if (sort) setFilters(defaultFilters);
    }, [sort]);

    const handleClickButton = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const handleClear = () => {
        setFilters(defaultFilters);
    };

    const handleChange = (key: string, value: string | number | Dayjs | undefined | null) =>
        setFilters({ ...filters, [key]: value });

    const handleValidate = () => {
        setUrlFilters(transformFiltersToURL(filters));
        setSort('');
        setOpen(false);
    };

    return (
        <>
            <Button variant="contained" onClick={handleClickButton}>
                Filtrer
            </Button>

            <Dialog open={open} onClose={handleClose}>
                <DialogTitle>Filtrer les boutiques</DialogTitle>

                <DialogContent>
                    <TextField
                        label="Nombre minimum de catégories distinctes"
                        type="number"
                        fullWidth
                        value={filters.distinctCategories}
                        onChange={(e) => handleChange('distinctCategories', e.target.value)}
                    />
                </DialogContent>

                <DialogContent>
                    <TextField
                        label="Nombre minimum de produits"
                        type="number"
                        fullWidth
                        value={filters.minProducts || ''}
                        onChange={(e) => handleChange('minProducts', e.target.value ? parseInt(e.target.value) : null)}
                    />
                </DialogContent>

                <DialogContent>
                    <FormControl fullWidth sx={{ marginTop: 2 }}>
                        <InputLabel id="demo-simple-select-label">Congé</InputLabel>
                        <Select
                            labelId="demo-simple-select-label"
                            id="demo-simple-select"
                            value={filters.inVacations}
                            label="Congé"
                            onChange={(e) => handleChange('inVacations', e.target.value)}
                        >
                            <MenuItem value="">
                                <em>Aucun</em>
                            </MenuItem>
                            <MenuItem value="true">En congé</MenuItem>
                            <MenuItem value="false">Pas en congé</MenuItem>
                        </Select>
                    </FormControl>
                </DialogContent>

                <DialogContent>
                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                        <DesktopDatePicker
                            label="Créée après"
                            inputFormat="DD/MM/YYYY"
                            value={filters.createdAfter}
                            onChange={(v: Dayjs | null) => handleChange('createdAfter', v)}
                            renderInput={(params) => <TextField {...params} />}
                        />
                    </LocalizationProvider>
                </DialogContent>

                <DialogContent>
                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                        <DesktopDatePicker
                            label="Créée avant"
                            inputFormat="DD/MM/YYYY"
                            value={filters.createdBefore}
                            onChange={(v: Dayjs | null) => handleChange('createdBefore', v)}
                            renderInput={(params) => <TextField {...params} />}
                        />
                    </LocalizationProvider>
                </DialogContent>

                <DialogActions>
                    <Button autoFocus onClick={handleClear}>
                        Effacer
                    </Button>
                    <Button autoFocus onClick={handleClose}>
                        Annuler
                    </Button>
                    <Button onClick={handleValidate}>Valider</Button>
                </DialogActions>
            </Dialog>
        </>
    );
};

export default Filters;
