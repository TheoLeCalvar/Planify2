import {
    Form,
    Outlet,
    redirect,
    useNavigate,
    useOutletContext,
    useSubmit,
} from "react-router-dom";
import { Box, TextField, Button, Stack } from "@mui/material";
import LoadingButton from "@mui/lab/LoadingButton";
import { useRef, useState } from "react";

export async function action({ request, params }) {
    const formData = await request.formData();
    const updates = Object.fromEntries(formData);
    const delay = () => {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve("Résolu après 2 secondes");
            }, 2000); // 2000 millisecondes = 2 secondes
        });
    };

    await delay();
    console.log(updates);
    return redirect("..");
}

export default function UESettings() {
    const context = useOutletContext();

    const ue = context.ue;
    const navigate = useNavigate();
    const sumbit = useSubmit();

    const form = useRef(null);

    const [loading, setLoading] = useState(false);

    const handleSumbit = async () => {
        setLoading(true);
        sumbit(form.current);
    };

    return (
        <>
            <Form method="post" ref={form}>
                <TextField
                    name="name"
                    label="Nom"
                    defaultValue={ue.name}
                    margin="normal"
                    fullWidth
                    required
                />
                <TextField
                    name="description"
                    label="Description"
                    defaultValue={ue.description}
                    multiline
                    minRows={3}
                    margin="normal"
                    fullWidth
                    required
                />
                <Stack
                    spacing={2}
                    direction={"row"}
                    justifyContent={"flex-end"}
                >
                    <Button
                        variant="outlined"
                        color="secondary"
                        onClick={() => navigate("..")}
                    >
                        Annuler
                    </Button>
                    <LoadingButton
                        variant="contained"
                        color="primary"
                        onClick={handleSumbit}
                        loading={loading}
                        loadingPosition="start"
                    >
                        Sauvegarder
                    </LoadingButton>
                </Stack>
            </Form>
            <Outlet context={context} />
        </>
    );
}
